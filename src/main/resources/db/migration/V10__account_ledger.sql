CREATE TABLE account_ledger (
    ledger_id           VARCHAR(36)   PRIMARY KEY,
    account_id          VARCHAR(36)   NOT NULL REFERENCES account_master(account_id),
    transaction_ref     VARCHAR(100)  NOT NULL,
    value_date          DATE          NOT NULL,
    posting_date        TIMESTAMP     NOT NULL DEFAULT NOW(),
    debit_amount        NUMERIC(20,2) NOT NULL DEFAULT 0,
    credit_amount       NUMERIC(20,2) NOT NULL DEFAULT 0,
    running_balance     NUMERIC(20,2) NOT NULL,
    narrative           VARCHAR(500),
    channel_code        VARCHAR(20),
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ledger_account ON account_ledger(account_id, value_date DESC);
CREATE INDEX idx_ledger_txn_ref ON account_ledger(transaction_ref);
