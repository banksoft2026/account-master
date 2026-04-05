CREATE TABLE account_earmark (
    earmark_id          VARCHAR(36)   PRIMARY KEY,
    account_id          VARCHAR(36)   NOT NULL REFERENCES account_master(account_id),
    earmark_amount      NUMERIC(20,2) NOT NULL,
    earmark_type        VARCHAR(30)   NOT NULL,  -- HOLD | GUARANTEE | COLLATERAL | PENDING_DEBIT
    reference_id        VARCHAR(100),
    description         VARCHAR(200),
    is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
    effective_from      TIMESTAMP     NOT NULL DEFAULT NOW(),
    effective_to        TIMESTAMP,
    created_by          VARCHAR(36)   NOT NULL,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by          VARCHAR(36),
    updated_at          TIMESTAMP,
    version             INTEGER       NOT NULL DEFAULT 1
);
CREATE INDEX idx_earmark_account ON account_earmark(account_id);
CREATE INDEX idx_earmark_active  ON account_earmark(account_id) WHERE is_active = TRUE;
