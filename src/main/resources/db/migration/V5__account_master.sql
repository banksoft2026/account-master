CREATE TABLE account_master (
    account_id          VARCHAR(36)   PRIMARY KEY,
    account_number      VARCHAR(34)   NOT NULL UNIQUE,   -- IBAN or internal number
    account_type        VARCHAR(20)   NOT NULL,
    product_id          VARCHAR(36)   NOT NULL REFERENCES account_product(product_id),
    product_code        VARCHAR(30)   NOT NULL,
    currency_code       VARCHAR(3)    NOT NULL,
    account_name        VARCHAR(200)  NOT NULL,
    short_name          VARCHAR(50),
    customer_id         VARCHAR(36)   NOT NULL,   -- ref to customer-entity service
    entity_id           VARCHAR(36)   NOT NULL,   -- ref to customer-entity service
    branch_code         VARCHAR(20)   NOT NULL,   -- ref to cbs-maintenance service
    purpose_code        VARCHAR(30),              -- OPERATING | PAYROLL | ESCROW | SAVINGS | INVESTMENT
    risk_category       VARCHAR(10)   DEFAULT 'LOW',  -- LOW | MEDIUM | HIGH
    ownership_type      VARCHAR(20)   DEFAULT 'SOLE',  -- SOLE | JOINT | CORPORATE
    relationship_manager_id VARCHAR(36),
    account_status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING_ACTIVATION',
    opening_date        DATE,
    closing_date        DATE,
    close_reason        VARCHAR(200),
    closed_by           VARCHAR(36),
    dormancy_date       DATE,
    freeze_reason       VARCHAR(200),
    -- Balance cache (denormalised mirror of account_balance for fast reads)
    ledger_balance      NUMERIC(20,2) NOT NULL DEFAULT 0,
    available_balance   NUMERIC(20,2) NOT NULL DEFAULT 0,
    earmarked_amount    NUMERIC(20,2) NOT NULL DEFAULT 0,
    uncollected_amount  NUMERIC(20,2) NOT NULL DEFAULT 0,
    overdraft_utilised  NUMERIC(20,2) NOT NULL DEFAULT 0,
    overdraft_limit_cached NUMERIC(20,2) NOT NULL DEFAULT 0,
    created_by          VARCHAR(36)   NOT NULL,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by          VARCHAR(36),
    updated_at          TIMESTAMP,
    version             INTEGER       NOT NULL DEFAULT 1,
    CONSTRAINT chk_account_status CHECK (account_status IN (
        'PENDING_ACTIVATION','ACTIVE','DORMANT','FROZEN','SUSPENDED','CLOSED','UNCLAIMED'
    ))
);
CREATE INDEX idx_acct_product    ON account_master(product_id);
CREATE INDEX idx_acct_entity     ON account_master(entity_id);
CREATE INDEX idx_acct_customer   ON account_master(customer_id);
CREATE INDEX idx_acct_branch     ON account_master(branch_code);
CREATE INDEX idx_acct_status     ON account_master(account_status);
CREATE INDEX idx_acct_currency   ON account_master(currency_code);
