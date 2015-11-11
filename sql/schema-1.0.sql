CREATE TABLE agent_area_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE agent_area_mapping
(
	    id INT PRIMARY KEY NOT NULL,
	    agent_id INT NOT NULL,
	    area_id INT NOT NULL
);
CREATE TABLE agents_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE areas
(
	    id INT PRIMARY KEY NOT NULL,
	    name VARCHAR(128) NOT NULL,
	    company_id INT NOT NULL,
	    code VARCHAR(5) NOT NULL,
	    city VARCHAR(45) NOT NULL,
	    id_sequence INT DEFAULT 1 NOT NULL
);
CREATE TABLE areas_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE companies
(
	    id INT PRIMARY KEY NOT NULL,
	    name VARCHAR(128) NOT NULL,
	    company_owner VARCHAR(128) NOT NULL,
	    contact_no BIGINT NOT NULL,
	    address VARCHAR NOT NULL,
	    receipt_sequence BIGINT DEFAULT 1 NOT NULL
);
CREATE TABLE companies_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE company_statistics
(
	    id INT DEFAULT nextval('company_statistics_id_seq'::regclass) NOT NULL,
	    company_id INT NOT NULL,
	    month TIMESTAMP NOT NULL,
	    collected_amount INT,
	    closing_balance INT
);
CREATE TABLE company_statistics_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE connections
(
	    id INT PRIMARY KEY NOT NULL,
	    customer_id INT NOT NULL,
	    setup_box_id VARCHAR(300),
	    plan_id INT NOT NULL,
	    discount INT DEFAULT 0 NOT NULL,
	    installation_date TIMESTAMP NOT NULL,
	    status VARCHAR(10) NOT NULL,
	    caf_id VARCHAR(300),
	    id_proof VARCHAR(300),
	    company_id INT NOT NULL,
	    box_serial_no VARCHAR(128)
);
CREATE TABLE connections_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE customers
(
	    id INT PRIMARY KEY NOT NULL,
	    name VARCHAR(128) NOT NULL,
	    mobile_no BIGINT,
	    email_id VARCHAR(128),
	    address VARCHAR(300) NOT NULL,
	    company_id INT NOT NULL,
	    area_id INT NOT NULL,
	    house_no VARCHAR NOT NULL,
	    balance_amount INT DEFAULT 0 NOT NULL
);
CREATE TABLE customers_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE notifications
(
	    id INT DEFAULT nextval('notifications_id_seq'::regclass) NOT NULL,
	    notification VARCHAR NOT NULL,
	    got_on TIMESTAMP,
	    company_id INT NOT NULL
);
CREATE TABLE notifications_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE payments
(
	    id INT PRIMARY KEY NOT NULL,
	    customer_id INT NOT NULL,
	    paid_amount INT NOT NULL,
	    paid_on TIMESTAMP DEFAULT now() NOT NULL,
	    agent_id INT NOT NULL,
	    discounted_amount INT NOT NULL,
	    remarks VARCHAR,
	    company_id INT NOT NULL,
	    receipt_no VARCHAR(32) NOT NULL
);
CREATE TABLE payments_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE plans
(
	    id INT PRIMARY KEY NOT NULL,
	    name VARCHAR(128) NOT NULL,
	    amount INT NOT NULL,
	    no_of_months INT NOT NULL,
	    company_id INT NOT NULL
);
CREATE TABLE plans_id_seq
(
	    sequence_name VARCHAR NOT NULL,
	    last_value BIGINT NOT NULL,
	    start_value BIGINT NOT NULL,
	    increment_by BIGINT NOT NULL,
	    max_value BIGINT NOT NULL,
	    min_value BIGINT NOT NULL,
	    cache_value BIGINT NOT NULL,
	    log_cnt BIGINT NOT NULL,
	    is_cycled BOOL NOT NULL,
	    is_called BOOL NOT NULL
);
CREATE TABLE users
(
	    id INT PRIMARY KEY NOT NULL,
	    name VARCHAR(128) NOT NULL,
	    company_id INT NOT NULL,
	    login_id VARCHAR(45) NOT NULL,
	    password VARCHAR(255) NOT NULL,
	    account_type VARCHAR(45) NOT NULL,
	    contact_no BIGINT NOT NULL,
	    email VARCHAR(256) NOT NULL,
	    address VARCHAR(512) NOT NULL
);
ALTER TABLE agent_area_mapping ADD FOREIGN KEY ( area_id ) REFERENCES areas ( id );
ALTER TABLE agent_area_mapping ADD FOREIGN KEY ( agent_id ) REFERENCES users ( id );
ALTER TABLE areas ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
CREATE UNIQUE INDEX index_unique_areas_code ON areas ( company_id, code );
CREATE UNIQUE INDEX index_unique_plans_name ON areas ( company_id, name );
CREATE UNIQUE INDEX index_unique_company_name ON companies ( name );
ALTER TABLE company_statistics ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
ALTER TABLE connections ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
ALTER TABLE connections ADD FOREIGN KEY ( customer_id ) REFERENCES customers ( id );
ALTER TABLE customers ADD FOREIGN KEY ( area_id ) REFERENCES areas ( id );
ALTER TABLE customers ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
CREATE UNIQUE INDEX index_unique_customers_mobile ON customers ( mobile_no, company_id );
ALTER TABLE notifications ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
ALTER TABLE payments ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
ALTER TABLE payments ADD FOREIGN KEY ( customer_id ) REFERENCES customers ( id );
ALTER TABLE payments ADD FOREIGN KEY ( agent_id ) REFERENCES users ( id );
ALTER TABLE plans ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
ALTER TABLE users ADD FOREIGN KEY ( company_id ) REFERENCES companies ( id );
CREATE UNIQUE INDEX index_unique_areas_login_id ON users ( login_id );

