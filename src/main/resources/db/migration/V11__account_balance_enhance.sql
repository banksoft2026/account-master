ALTER TABLE account_balance
    ADD COLUMN IF NOT EXISTS balance_as_at       TIMESTAMP,
    ADD COLUMN IF NOT EXISTS balance_version     BIGINT        NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS last_txn_id         VARCHAR(36),
    ADD COLUMN IF NOT EXISTS last_txn_at         TIMESTAMP,
    ADD COLUMN IF NOT EXISTS txn_sequence        BIGINT        NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS accrued_interest_cr NUMERIC(20,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS accrued_interest_dr NUMERIC(20,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS float_amount        NUMERIC(20,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS shadow_balance      NUMERIC(20,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS earmark_count       SMALLINT      NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS uncollected_count   SMALLINT      NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_bal_od_utilised ON account_balance(account_id) WHERE overdraft_utilised > 0;
CREATE INDEX IF NOT EXISTS idx_bal_earmarked ON account_balance(account_id) WHERE earmark_count > 0;
