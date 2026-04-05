CREATE TABLE account_balance (
    balance_id          VARCHAR(36)   PRIMARY KEY,
    account_id          VARCHAR(36)   NOT NULL UNIQUE REFERENCES account_master(account_id),
    ledger_balance      NUMERIC(20,2) NOT NULL DEFAULT 0,
    available_balance   NUMERIC(20,2) NOT NULL DEFAULT 0,
    earmarked_amount    NUMERIC(20,2) NOT NULL DEFAULT 0,
    uncollected_amount  NUMERIC(20,2) NOT NULL DEFAULT 0,
    overdraft_utilised  NUMERIC(20,2) NOT NULL DEFAULT 0,
    overdraft_limit     NUMERIC(20,2) NOT NULL DEFAULT 0,
    currency_code       VARCHAR(3)    NOT NULL,
    balance_date        DATE          NOT NULL DEFAULT CURRENT_DATE,
    value_date          TIMESTAMP     NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP,
    version             INTEGER       NOT NULL DEFAULT 1
);
CREATE INDEX idx_balance_account ON account_balance(account_id);
