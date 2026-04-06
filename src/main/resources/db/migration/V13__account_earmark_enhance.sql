ALTER TABLE account_earmark
    ADD COLUMN IF NOT EXISTS earmark_ref     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS source_module   VARCHAR(50) NOT NULL DEFAULT 'MANUAL_OPS',
    ADD COLUMN IF NOT EXISTS earmark_reason  VARCHAR(500),
    ADD COLUMN IF NOT EXISTS earmark_status  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS expiry_at       TIMESTAMP,
    ADD COLUMN IF NOT EXISTS released_amount NUMERIC(20,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS released_at     TIMESTAMP,
    ADD COLUMN IF NOT EXISTS released_by     VARCHAR(36),
    ADD COLUMN IF NOT EXISTS release_reason  VARCHAR(500),
    ADD COLUMN IF NOT EXISTS release_txn_id  VARCHAR(36),
    ADD COLUMN IF NOT EXISTS source_txn_id   VARCHAR(36),
    ADD COLUMN IF NOT EXISTS approved_by     VARCHAR(36);

CREATE UNIQUE INDEX IF NOT EXISTS idx_earmark_idempotent
    ON account_earmark(account_id, earmark_ref)
    WHERE earmark_status IN ('ACTIVE','PARTIALLY_RELEASED') AND earmark_ref IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_earmark_expiry
    ON account_earmark(expiry_at)
    WHERE earmark_status = 'ACTIVE' AND expiry_at IS NOT NULL;
