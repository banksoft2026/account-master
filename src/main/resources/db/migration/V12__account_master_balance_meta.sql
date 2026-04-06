ALTER TABLE account_master
    ADD COLUMN IF NOT EXISTS balance_as_at      TIMESTAMP,
    ADD COLUMN IF NOT EXISTS balance_version    BIGINT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS last_txn_id        VARCHAR(36),
    ADD COLUMN IF NOT EXISTS last_txn_at        TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_acct_balance_version ON account_master(account_id, balance_version);
CREATE INDEX IF NOT EXISTS idx_acct_overdrawn ON account_master(account_id) WHERE overdraft_utilised > 0;
