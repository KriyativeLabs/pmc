--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: audit; Type: SCHEMA; Schema: -; Owner: paymycable
--

CREATE SCHEMA audit;


ALTER SCHEMA audit OWNER TO paymycable;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: hstore; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS hstore WITH SCHEMA public;


--
-- Name: EXTENSION hstore; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION hstore IS 'data type for storing sets of (key, value) pairs';


SET search_path = audit, pg_catalog;

--
-- Name: audit_table(regclass); Type: FUNCTION; Schema: audit; Owner: paymycable
--

CREATE FUNCTION audit_table(target_table regclass) RETURNS void
    LANGUAGE sql
    AS $_$
SELECT audit.audit_table($1, BOOLEAN 't', BOOLEAN 't');
$_$;


ALTER FUNCTION audit.audit_table(target_table regclass) OWNER TO paymycable;

--
-- Name: FUNCTION audit_table(target_table regclass); Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON FUNCTION audit_table(target_table regclass) IS '
Add auditing support to the given table. Row-level changes will be logged with full client query text. No cols are ignored.
';


--
-- Name: audit_table(regclass, boolean, boolean); Type: FUNCTION; Schema: audit; Owner: paymycable
--

CREATE FUNCTION audit_table(target_table regclass, audit_rows boolean, audit_query_text boolean) RETURNS void
    LANGUAGE sql
    AS $_$
SELECT audit.audit_table($1, $2, $3, ARRAY[]::text[]);
$_$;


ALTER FUNCTION audit.audit_table(target_table regclass, audit_rows boolean, audit_query_text boolean) OWNER TO paymycable;

--
-- Name: audit_table(regclass, boolean, boolean, text[]); Type: FUNCTION; Schema: audit; Owner: paymycable
--

CREATE FUNCTION audit_table(target_table regclass, audit_rows boolean, audit_query_text boolean, ignored_cols text[]) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
  stm_targets text = 'INSERT OR UPDATE OR DELETE OR TRUNCATE';
  _q_txt text;
  _ignored_cols_snip text = '';
BEGIN
    EXECUTE 'DROP TRIGGER IF EXISTS audit_trigger_row ON ' || quote_ident(target_table::TEXT);
    EXECUTE 'DROP TRIGGER IF EXISTS audit_trigger_stm ON ' || quote_ident(target_table::TEXT);

    IF audit_rows THEN
        IF array_length(ignored_cols,1) > 0 THEN
            _ignored_cols_snip = ', ' || quote_literal(ignored_cols);
        END IF;
        _q_txt = 'CREATE TRIGGER audit_trigger_row AFTER INSERT OR UPDATE OR DELETE ON ' ||
                 quote_ident(target_table::TEXT) ||
                 ' FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func(' ||
                 quote_literal(audit_query_text) || _ignored_cols_snip || ');';
        RAISE NOTICE '%',_q_txt;
        EXECUTE _q_txt;
        stm_targets = 'TRUNCATE';
    ELSE
    END IF;

    _q_txt = 'CREATE TRIGGER audit_trigger_stm AFTER ' || stm_targets || ' ON ' ||
             target_table ||
             ' FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('||
             quote_literal(audit_query_text) || ');';
    RAISE NOTICE '%',_q_txt;
    EXECUTE _q_txt;

END;
$$;


ALTER FUNCTION audit.audit_table(target_table regclass, audit_rows boolean, audit_query_text boolean, ignored_cols text[]) OWNER TO paymycable;

--
-- Name: if_modified_func(); Type: FUNCTION; Schema: audit; Owner: paymycable
--

CREATE FUNCTION if_modified_func() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    SET search_path TO pg_catalog, public
    AS $$
DECLARE
    audit_row audit.logged_actions;
    include_values boolean;
    log_diffs boolean;
    h_old hstore;
    h_new hstore;
    excluded_cols text[] = ARRAY[]::text[];
BEGIN
    IF TG_WHEN <> 'AFTER' THEN
        RAISE EXCEPTION 'audit.if_modified_func() may only run as an AFTER trigger';
    END IF;

    audit_row = ROW(
        nextval('audit.logged_actions_event_id_seq'), -- event_id
        TG_TABLE_SCHEMA::text,                        -- schema_name
        TG_TABLE_NAME::text,                          -- table_name
        TG_RELID,                                     -- relation OID for much quicker searches
        session_user::text,                           -- session_user_name
        current_timestamp,                            -- action_tstamp_tx
        statement_timestamp(),                        -- action_tstamp_stm
        clock_timestamp(),                            -- action_tstamp_clk
        txid_current(),                               -- transaction ID
        current_setting('application_name'),          -- client application
        inet_client_addr(),                           -- client_addr
        inet_client_port(),                           -- client_port
        current_query(),                              -- top-level query or queries (if multistatement) from client
        substring(TG_OP,1,1),                         -- action
        NULL, NULL, NULL,                             -- entity_id, row_data, changed_fields
        'f'                                           -- statement_only
        );

    IF NOT TG_ARGV[0]::boolean IS DISTINCT FROM 'f'::boolean THEN
        audit_row.client_query = NULL;
    END IF;

    IF TG_ARGV[1] IS NOT NULL THEN
        excluded_cols = TG_ARGV[1]::text[];
    END IF;

    IF (TG_OP = 'UPDATE' AND TG_LEVEL = 'ROW') THEN
        audit_row.entity_id = OLD.id;
        audit_row.row_data = hstore(OLD.*) - excluded_cols;
        audit_row.changed_fields =  (hstore(NEW.*) - audit_row.row_data) - excluded_cols;
        IF audit_row.changed_fields = hstore('') THEN
            -- All changed fields are ignored. Skip this update.
            RETURN NULL;
        END IF;
    ELSIF (TG_OP = 'DELETE' AND TG_LEVEL = 'ROW') THEN
        audit_row.entity_id = OLD.id;
        audit_row.row_data = hstore(OLD.*) - excluded_cols;
    ELSIF (TG_OP = 'INSERT' AND TG_LEVEL = 'ROW') THEN
      audit_row.entity_id = NEW.id;
        audit_row.row_data = hstore(NEW.*) - excluded_cols;
    ELSIF (TG_LEVEL = 'STATEMENT' AND TG_OP IN ('INSERT','UPDATE','DELETE','TRUNCATE')) THEN
        audit_row.statement_only = 't';
    ELSE
        RAISE EXCEPTION '[audit.if_modified_func] - Trigger func added as trigger for unhandled case: %, %',TG_OP, TG_LEVEL;
        RETURN NULL;
    END IF;
    INSERT INTO audit.logged_actions VALUES (audit_row.*);
    RETURN NULL;
END;
$$;


ALTER FUNCTION audit.if_modified_func() OWNER TO paymycable;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: logged_actions; Type: TABLE; Schema: audit; Owner: paymycable; Tablespace: 
--

CREATE TABLE logged_actions (
    event_id bigint NOT NULL,
    schema_name text NOT NULL,
    table_name text NOT NULL,
    relid oid NOT NULL,
    session_user_name text,
    action_tstamp_tx timestamp with time zone NOT NULL,
    action_tstamp_stm timestamp with time zone NOT NULL,
    action_tstamp_clk timestamp with time zone NOT NULL,
    transaction_id bigint,
    application_name text,
    client_addr inet,
    client_port integer,
    client_query text,
    action text NOT NULL,
    entity_id integer,
    row_data public.hstore,
    changed_fields public.hstore,
    statement_only boolean NOT NULL,
    CONSTRAINT logged_actions_action_check CHECK ((action = ANY (ARRAY['I'::text, 'D'::text, 'U'::text, 'T'::text])))
);


ALTER TABLE audit.logged_actions OWNER TO paymycable;

--
-- Name: TABLE logged_actions; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON TABLE logged_actions IS 'History of auditable actions on audited tables, from audit.if_modified_func()';


--
-- Name: COLUMN logged_actions.event_id; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.event_id IS 'Unique identifier for each auditable event';


--
-- Name: COLUMN logged_actions.schema_name; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.schema_name IS 'Database schema audited table for this event is in';


--
-- Name: COLUMN logged_actions.table_name; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.table_name IS 'Non-schema-qualified table name of table event occured in';


--
-- Name: COLUMN logged_actions.relid; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.relid IS 'Table OID. Changes with drop/create. Get with ''tablename''::regclass';


--
-- Name: COLUMN logged_actions.session_user_name; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.session_user_name IS 'Login / session user whose statement caused the audited event';


--
-- Name: COLUMN logged_actions.action_tstamp_tx; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.action_tstamp_tx IS 'Transaction start timestamp for tx in which audited event occurred';


--
-- Name: COLUMN logged_actions.action_tstamp_stm; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.action_tstamp_stm IS 'Statement start timestamp for tx in which audited event occurred';


--
-- Name: COLUMN logged_actions.action_tstamp_clk; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.action_tstamp_clk IS 'Wall clock time at which audited event''s trigger call occurred';


--
-- Name: COLUMN logged_actions.transaction_id; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.transaction_id IS 'Identifier of transaction that made the change. May wrap, but unique paired with action_tstamp_tx.';


--
-- Name: COLUMN logged_actions.application_name; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.application_name IS 'Application name set when this audit event occurred. Can be changed in-session by client.';


--
-- Name: COLUMN logged_actions.client_addr; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.client_addr IS 'IP address of client that issued query. Null for unix domain socket.';


--
-- Name: COLUMN logged_actions.client_port; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.client_port IS 'Remote peer IP port address of client that issued query. Undefined for unix socket.';


--
-- Name: COLUMN logged_actions.client_query; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.client_query IS 'Top-level query that caused this auditable event. May be more than one statement.';


--
-- Name: COLUMN logged_actions.action; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.action IS 'Action type; I = insert, D = delete, U = update, T = truncate';


--
-- Name: COLUMN logged_actions.row_data; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.row_data IS 'Record value. Null for statement-level trigger. For INSERT this is the new tuple. For DELETE and UPDATE it is the old tuple.';


--
-- Name: COLUMN logged_actions.changed_fields; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.changed_fields IS 'New values of fields changed by UPDATE. Null except for row-level UPDATE events.';


--
-- Name: COLUMN logged_actions.statement_only; Type: COMMENT; Schema: audit; Owner: paymycable
--

COMMENT ON COLUMN logged_actions.statement_only IS '''t'' if audit event is from an FOR EACH STATEMENT trigger, ''f'' for FOR EACH ROW';


--
-- Name: logged_actions_event_id_seq; Type: SEQUENCE; Schema: audit; Owner: paymycable
--

CREATE SEQUENCE logged_actions_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE audit.logged_actions_event_id_seq OWNER TO paymycable;

--
-- Name: logged_actions_event_id_seq; Type: SEQUENCE OWNED BY; Schema: audit; Owner: paymycable
--

ALTER SEQUENCE logged_actions_event_id_seq OWNED BY logged_actions.event_id;


SET search_path = public, pg_catalog;

--
-- Name: areas; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE areas (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    company_id integer NOT NULL,
    code character varying(5) NOT NULL,
    city character varying(45) NOT NULL,
    id_sequence integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.areas OWNER TO paymycable;

--
-- Name: areas_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE areas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.areas_id_seq OWNER TO paymycable;

--
-- Name: areas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE areas_id_seq OWNED BY areas.id;


--
-- Name: companies; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE companies (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    company_owner character varying(128) NOT NULL,
    company_code character varying(10) NOT NULL,
    contact_no bigint NOT NULL,
    address text NOT NULL,
    sms_count integer DEFAULT 0 NOT NULL,
    price_per_customer integer NOT NULL,
    receipt_sequence bigint DEFAULT 1 NOT NULL,
    customer_seq_no integer DEFAULT 0,
    last_bill_generated_on timestamp without time zone DEFAULT now(),
    bill_status boolean DEFAULT false,
    sms_enabled boolean DEFAULT true NOT NULL,
    is_cable_network boolean DEFAULT true NOT NULL
);


ALTER TABLE public.companies OWNER TO paymycable;

--
-- Name: companies_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE companies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.companies_id_seq OWNER TO paymycable;

--
-- Name: companies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE companies_id_seq OWNED BY companies.id;


--
-- Name: company_statistics; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE company_statistics (
    id integer NOT NULL,
    company_id integer NOT NULL,
    month timestamp without time zone NOT NULL,
    collected_amount integer NOT NULL,
    closing_balance integer NOT NULL
);


ALTER TABLE public.company_statistics OWNER TO paymycable;

--
-- Name: company_statistics_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE company_statistics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.company_statistics_id_seq OWNER TO paymycable;

--
-- Name: company_statistics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE company_statistics_id_seq OWNED BY company_statistics.id;


--
-- Name: connections; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE connections (
    id integer NOT NULL,
    customer_id integer NOT NULL,
    setup_box_id character varying(300),
    box_serial_no character varying(300),
    plan_id integer NOT NULL,
    discount integer DEFAULT 0 NOT NULL,
    installation_date timestamp without time zone NOT NULL,
    status character varying(10) NOT NULL,
    caf_id character varying(300),
    id_proof character varying(300),
    company_id integer NOT NULL
);


ALTER TABLE public.connections OWNER TO paymycable;

--
-- Name: connections_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE connections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.connections_id_seq OWNER TO paymycable;

--
-- Name: connections_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE connections_id_seq OWNED BY connections.id;


--
-- Name: credits; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE credits (
    id integer NOT NULL,
    customer_id integer NOT NULL,
    amount integer NOT NULL,
    company_id integer NOT NULL,
    credited_on timestamp without time zone NOT NULL
);


ALTER TABLE public.credits OWNER TO paymycable;

--
-- Name: credits_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE credits_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.credits_id_seq OWNER TO paymycable;

--
-- Name: credits_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE credits_id_seq OWNED BY credits.id;


--
-- Name: customers; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE customers (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    mobile_no bigint,
    email_id character varying(128),
    address character varying(300) NOT NULL,
    company_id integer NOT NULL,
    area_id integer NOT NULL,
    house_no character varying NOT NULL,
    balance_amount integer DEFAULT 0 NOT NULL,
    created_by integer,
    updated_by integer
);


ALTER TABLE public.customers OWNER TO paymycable;

--
-- Name: customers_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE customers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customers_id_seq OWNER TO paymycable;

--
-- Name: customers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE customers_id_seq OWNED BY customers.id;


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE notifications (
    id integer NOT NULL,
    notification character varying NOT NULL,
    got_on timestamp without time zone,
    company_id integer NOT NULL
);


ALTER TABLE public.notifications OWNER TO paymycable;

--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO paymycable;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE notifications_id_seq OWNED BY notifications.id;


--
-- Name: payments; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE payments (
    id integer NOT NULL,
    customer_id integer NOT NULL,
    paid_amount integer NOT NULL,
    discounted_amount integer NOT NULL,
    paid_on timestamp without time zone DEFAULT now() NOT NULL,
    agent_id integer NOT NULL,
    remarks text,
    company_id integer NOT NULL,
    receipt_no character varying(32) NOT NULL
);


ALTER TABLE public.payments OWNER TO paymycable;

--
-- Name: payments_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE payments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.payments_id_seq OWNER TO paymycable;

--
-- Name: payments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE payments_id_seq OWNED BY payments.id;


--
-- Name: plans; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE plans (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    amount integer NOT NULL,
    no_of_months integer NOT NULL,
    company_id integer NOT NULL
);


ALTER TABLE public.plans OWNER TO paymycable;

--
-- Name: plans_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE plans_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.plans_id_seq OWNER TO paymycable;

--
-- Name: plans_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE plans_id_seq OWNED BY plans.id;


--
-- Name: user_area_mapping; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE user_area_mapping (
    id integer DEFAULT nextval('plans_id_seq'::regclass) NOT NULL,
    user_id integer NOT NULL,
    area_id integer NOT NULL
);


ALTER TABLE public.user_area_mapping OWNER TO paymycable;

--
-- Name: user_area_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE user_area_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_area_id_seq OWNER TO paymycable;

--
-- Name: user_area_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE user_area_id_seq OWNED BY user_area_mapping.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    company_id integer NOT NULL,
    login_id character varying(45) NOT NULL,
    password character varying(255) NOT NULL,
    contact_no bigint NOT NULL,
    email character varying(128),
    address character varying(512),
    account_type character varying(10) NOT NULL
);


ALTER TABLE public.users OWNER TO paymycable;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: paymycable
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO paymycable;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: paymycable
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


SET search_path = audit, pg_catalog;

--
-- Name: event_id; Type: DEFAULT; Schema: audit; Owner: paymycable
--

ALTER TABLE ONLY logged_actions ALTER COLUMN event_id SET DEFAULT nextval('logged_actions_event_id_seq'::regclass);


SET search_path = public, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY areas ALTER COLUMN id SET DEFAULT nextval('areas_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY companies ALTER COLUMN id SET DEFAULT nextval('companies_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY company_statistics ALTER COLUMN id SET DEFAULT nextval('company_statistics_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY connections ALTER COLUMN id SET DEFAULT nextval('connections_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY credits ALTER COLUMN id SET DEFAULT nextval('credits_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY customers ALTER COLUMN id SET DEFAULT nextval('customers_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY notifications ALTER COLUMN id SET DEFAULT nextval('notifications_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY payments ALTER COLUMN id SET DEFAULT nextval('payments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY plans ALTER COLUMN id SET DEFAULT nextval('plans_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


SET search_path = audit, pg_catalog;

--
-- Name: logged_actions_pkey; Type: CONSTRAINT; Schema: audit; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY logged_actions
    ADD CONSTRAINT logged_actions_pkey PRIMARY KEY (event_id);


SET search_path = public, pg_catalog;

--
-- Name: constraintname; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY plans
    ADD CONSTRAINT constraintname UNIQUE (name, company_id);


--
-- Name: credits_unique; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY credits
    ADD CONSTRAINT credits_unique UNIQUE (customer_id, credited_on);


--
-- Name: pk_areas; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY areas
    ADD CONSTRAINT pk_areas PRIMARY KEY (id);


--
-- Name: pk_companies; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY companies
    ADD CONSTRAINT pk_companies PRIMARY KEY (id);


--
-- Name: pk_connections; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY connections
    ADD CONSTRAINT pk_connections PRIMARY KEY (id);


--
-- Name: pk_customers; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY customers
    ADD CONSTRAINT pk_customers PRIMARY KEY (id);


--
-- Name: pk_payments; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY payments
    ADD CONSTRAINT pk_payments PRIMARY KEY (id);


--
-- Name: pk_plans; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY plans
    ADD CONSTRAINT pk_plans PRIMARY KEY (id);


--
-- Name: pk_user_area; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY user_area_mapping
    ADD CONSTRAINT pk_user_area PRIMARY KEY (id);


--
-- Name: pk_users; Type: CONSTRAINT; Schema: public; Owner: paymycable; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);


SET search_path = audit, pg_catalog;

--
-- Name: logged_actions_action_idx; Type: INDEX; Schema: audit; Owner: paymycable; Tablespace: 
--

CREATE INDEX logged_actions_action_idx ON logged_actions USING btree (action);


--
-- Name: logged_actions_action_tstamp_tx_stm_idx; Type: INDEX; Schema: audit; Owner: paymycable; Tablespace: 
--

CREATE INDEX logged_actions_action_tstamp_tx_stm_idx ON logged_actions USING btree (action_tstamp_stm);


--
-- Name: logged_actions_relid_idx; Type: INDEX; Schema: audit; Owner: paymycable; Tablespace: 
--

CREATE INDEX logged_actions_relid_idx ON logged_actions USING btree (relid);


SET search_path = public, pg_catalog;

--
-- Name: index_unique_areas_code; Type: INDEX; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE UNIQUE INDEX index_unique_areas_code ON areas USING btree (company_id, code);


--
-- Name: index_unique_areas_login_id; Type: INDEX; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE UNIQUE INDEX index_unique_areas_login_id ON users USING btree (login_id);


--
-- Name: index_unique_company_name; Type: INDEX; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE UNIQUE INDEX index_unique_company_name ON companies USING btree (name);


--
-- Name: index_unique_customers_mobile; Type: INDEX; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE UNIQUE INDEX index_unique_customers_mobile ON customers USING btree (mobile_no, company_id);


--
-- Name: index_unique_plans_name; Type: INDEX; Schema: public; Owner: paymycable; Tablespace: 
--

CREATE UNIQUE INDEX index_unique_plans_name ON areas USING btree (company_id, name);


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON areas FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON companies FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON company_statistics FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON connections FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON customers FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON notifications FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON payments FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON plans FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON user_area_mapping FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_row; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_row AFTER INSERT OR DELETE OR UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON areas FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON companies FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON company_statistics FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON connections FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON customers FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON notifications FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON payments FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON plans FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON user_area_mapping FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: audit_trigger_stm; Type: TRIGGER; Schema: public; Owner: paymycable
--

CREATE TRIGGER audit_trigger_stm AFTER TRUNCATE ON users FOR EACH STATEMENT EXECUTE PROCEDURE audit.if_modified_func('true');


--
-- Name: areas_company_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY areas
    ADD CONSTRAINT areas_company_fk FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: company_statistics_company_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY company_statistics
    ADD CONSTRAINT company_statistics_company_id_fkey FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: connections_company_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY connections
    ADD CONSTRAINT connections_company_fk FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: connections_customer_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY connections
    ADD CONSTRAINT connections_customer_fk FOREIGN KEY (customer_id) REFERENCES customers(id);


--
-- Name: credits_company_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY credits
    ADD CONSTRAINT credits_company_id_fkey FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: credits_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY credits
    ADD CONSTRAINT credits_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customers(id);


--
-- Name: customers_area_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY customers
    ADD CONSTRAINT customers_area_fk FOREIGN KEY (area_id) REFERENCES areas(id);


--
-- Name: customers_company_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY customers
    ADD CONSTRAINT customers_company_fk FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: customers_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY customers
    ADD CONSTRAINT customers_created_by_fkey FOREIGN KEY (created_by) REFERENCES users(id);


--
-- Name: customers_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY customers
    ADD CONSTRAINT customers_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES users(id);


--
-- Name: notifications_company_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY notifications
    ADD CONSTRAINT notifications_company_id_fkey FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: payments_company_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY payments
    ADD CONSTRAINT payments_company_fk FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: payments_customer_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY payments
    ADD CONSTRAINT payments_customer_fk FOREIGN KEY (customer_id) REFERENCES customers(id);


--
-- Name: payments_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY payments
    ADD CONSTRAINT payments_user_fk FOREIGN KEY (agent_id) REFERENCES users(id);


--
-- Name: plans_company_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY plans
    ADD CONSTRAINT plans_company_fk FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: user_area_mapping_area_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY user_area_mapping
    ADD CONSTRAINT user_area_mapping_area_fk FOREIGN KEY (area_id) REFERENCES areas(id);


--
-- Name: user_area_mapping_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY user_area_mapping
    ADD CONSTRAINT user_area_mapping_user_fk FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: users_company_fk; Type: FK CONSTRAINT; Schema: public; Owner: paymycable
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_company_fk FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: audit; Type: ACL; Schema: -; Owner: paymycable
--

REVOKE ALL ON SCHEMA audit FROM PUBLIC;
REVOKE ALL ON SCHEMA audit FROM paymycable;
GRANT ALL ON SCHEMA audit TO paymycable;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


SET search_path = audit, pg_catalog;

--
-- Name: logged_actions; Type: ACL; Schema: audit; Owner: paymycable
--

REVOKE ALL ON TABLE logged_actions FROM PUBLIC;
REVOKE ALL ON TABLE logged_actions FROM paymycable;
GRANT ALL ON TABLE logged_actions TO paymycable;


--
-- PostgreSQL database dump complete
--

