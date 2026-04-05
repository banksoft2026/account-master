CREATE TABLE account_parameter_override (
    override_id             VARCHAR(36)   PRIMARY KEY,
    account_id              VARCHAR(36)   NOT NULL REFERENCES account_master(account_id),
    param_key               VARCHAR(50)   NOT NULL,
    original_product_value  VARCHAR(100)  NOT NULL,
    overridden_value        VARCHAR(100)  NOT NULL,
    override_reason         VARCHAR(500)  NOT NULL,
    approved_by             VARCHAR(36)   NOT NULL,
    approved_at             TIMESTAMP     NOT NULL DEFAULT NOW(),
    approval_ref            VARCHAR(100),
    effective_from          TIMESTAMP     NOT NULL DEFAULT NOW(),
    effective_to            TIMESTAMP,
    is_active               BOOLEAN       NOT NULL DEFAULT TRUE,
    version                 INTEGER       NOT NULL DEFAULT 1
);
CREATE UNIQUE INDEX idx_override_active ON account_parameter_override(account_id, param_key) WHERE is_active = TRUE;
CREATE INDEX idx_override_account ON account_parameter_override(account_id);
