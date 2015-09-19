--- companies table ---
CREATE SEQUENCE public.companies_id_seq;

CREATE TABLE public.companies (
                id INTEGER NOT NULL DEFAULT nextval('public.companies_id_seq'),
                name VARCHAR(128) NOT NULL,
                company_owner VARCHAR(128) NOT NULL,
                company_code VARCHAR(10) NOT NULL,
                contact_no BIGINT NOT NULL,
                address TEXT NOT NULL,
                sms_count INTEGER NOT NULL DEFAULT 0,
                price_per_customer INTEGER NOT NULL,
                CONSTRAINT pk_companies PRIMARY KEY (id));

ALTER SEQUENCE public.companies_id_seq OWNED BY  public.companies.id;
CREATE UNIQUE INDEX index_unique_company_name ON public.companies (name);
--- end ---

--- users table ---
CREATE SEQUENCE public.users_id_seq;

CREATE TABLE public.users (
  id INTEGER NOT NULL DEFAULT nextval('public.users_id_seq'),
  name VARCHAR(128) NOT NULL,
  company_id INT NOT NULL,
  login_id VARCHAR(45) NOT NULL,
  password VARCHAR(255) NOT NULL,
  contact_no BIGINT NOT NULL,
  email VARCHAR(128),
  account_type VARCHAR(10) NOT NULL,
CONSTRAINT pk_users PRIMARY KEY (id));

ALTER SEQUENCE public.users_id_seq OWNED BY  public.users.id;
CREATE UNIQUE INDEX index_unique_areas_login_id ON public.users(login_id);

ALTER TABLE public.users ADD CONSTRAINT users_company_fk FOREIGN KEY (company_id)
REFERENCES public.companies(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
--- end ---

--- areas table ---
CREATE SEQUENCE public.areas_id_seq;

CREATE TABLE public.areas (
  id INTEGER NOT NULL DEFAULT nextval('public.areas_id_seq'),
  name VARCHAR(128) NOT NULL,
  company_id INT NOT NULL,
  code VARCHAR(5) NOT NULL,
  city VARCHAR(45) NOT NULL,
  id_sequence INT NOT NULL DEFAULT 1,
  CONSTRAINT pk_areas PRIMARY KEY (id));

ALTER SEQUENCE public.areas_id_seq OWNED BY  public.areas.id;
CREATE UNIQUE INDEX index_unique_areas_code ON public.areas(company_id, code);

ALTER TABLE public.areas ADD CONSTRAINT areas_company_fk FOREIGN KEY (company_id)
REFERENCES public.companies(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
--- end ---

--- customers ---
CREATE SEQUENCE public.customers_id_seq;

CREATE TABLE public.customers (
  id INTEGER NOT NULL DEFAULT nextval('public.customers_id_seq'),
  name VARCHAR(128) NOT NULL,
  mobile_no BIGINT,
  email_id VARCHAR(128),
  address VARCHAR(300) NOT NULL,
  company_id INT NOT NULL,
  area_id INT NOT NULL,
  house_no VARCHAR NOT NULL,
  balance_amount INT NOT NULL DEFAULT 0,
  CONSTRAINT pk_customers PRIMARY KEY (id));

ALTER SEQUENCE public.customers_id_seq OWNED BY  public.customers.id;
CREATE UNIQUE INDEX index_unique_customers_mobile ON public.customers(mobile_no,company_id);

ALTER TABLE public.customers ADD CONSTRAINT customers_company_fk FOREIGN KEY (company_id)
REFERENCES public.companies(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.customers ADD CONSTRAINT customers_area_fk FOREIGN KEY (area_id)
REFERENCES public.areas(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

--- end ---

--- plans table ---
CREATE SEQUENCE public.plans_id_seq;

CREATE TABLE public.plans (
  id INTEGER NOT NULL DEFAULT nextval('public.plans_id_seq'),
  name VARCHAR(128) NOT NULL,
  amount INT NOT NULL,
  no_of_months INT NOT NULL,
  company_id INT NOT NULL,
  CONSTRAINT pk_plans PRIMARY KEY (id));

ALTER SEQUENCE public.plans_id_seq OWNED BY  public.plans.id;
CREATE UNIQUE INDEX index_unique_plans_name ON public.areas(company_id, name);

ALTER TABLE public.plans ADD CONSTRAINT plans_company_fk FOREIGN KEY (company_id)
REFERENCES public.companies(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
--- end ---

--- users-area-mapping ---
CREATE SEQUENCE public.user_area_id_seq;

CREATE TABLE public.user_area_mapping (
  id INTEGER NOT NULL DEFAULT nextval('public.plans_id_seq'),
  user_id INT NOT NULL,
  area_id INT NOT NULL,
  CONSTRAINT pk_user_area PRIMARY KEY (id));

ALTER SEQUENCE public.user_area_id_seq OWNED BY  public.user_area_mapping.id;

ALTER TABLE public.user_area_mapping ADD CONSTRAINT user_area_mapping_user_fk FOREIGN KEY (user_id)
REFERENCES public.users(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.user_area_mapping ADD CONSTRAINT user_area_mapping_area_fk FOREIGN KEY (area_id)
REFERENCES public.areas(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

--- end ---

--- payments table ---
CREATE SEQUENCE public.payments_id_seq;

CREATE TABLE public.payments (
  id INTEGER NOT NULL DEFAULT nextval('public.payments_id_seq'),
  customer_id INT NOT NULL,
  paid_amount INT NOT NULL,
  discounted_amount INT NOT NULL,
  paid_on TIMESTAMP NOT NULL DEFAULT now(),
  user_id INT NOT NULL,
  remarks TEXT,
  company_id INT NOT NULL,
  CONSTRAINT pk_payments PRIMARY KEY (id));

ALTER SEQUENCE public.payments_id_seq OWNED BY  public.payments.id;

ALTER TABLE public.payments ADD CONSTRAINT payments_customer_fk FOREIGN KEY (customer_id)
REFERENCES public.customers(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.payments ADD CONSTRAINT payments_user_fk FOREIGN KEY (user_id)
REFERENCES public.users(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.payments ADD CONSTRAINT payments_company_fk FOREIGN KEY (company_id)
REFERENCES public.companies(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

--- end ---

--- connections table ---
CREATE SEQUENCE public.connections_id_seq;

CREATE TABLE public.connections (
  id INTEGER NOT NULL DEFAULT nextval('public.connections_id_seq'),
  customer_id INT NOT NULL,
  setup_box_id VARCHAR(300),
  plan_id INT NOT NULL,
  discount INT NOT NULL DEFAULT 0,
  installation_date TIMESTAMP NOT NULL,
  status VARCHAR(10) NOT NULL,
  caf_id VARCHAR(300),
  id_proof VARCHAR(300),
  company_id INT NOT NULL,
  CONSTRAINT pk_connections PRIMARY KEY (id));

ALTER SEQUENCE public.connections_id_seq OWNED BY  public.connections.id;

ALTER TABLE public.connections ADD CONSTRAINT connections_customer_fk FOREIGN KEY (customer_id)
REFERENCES public.customers(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.connections ADD CONSTRAINT connections_company_fk FOREIGN KEY (company_id)
REFERENCES public.companies(id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

--- end ---



























