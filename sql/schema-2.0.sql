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
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

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
    sms_enabled boolean DEFAULT true NOT NULL
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
    balance_amount integer DEFAULT 0 NOT NULL
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
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

