CREATE TABLE product_interest_tier (
    tier_id             VARCHAR(36)   PRIMARY KEY,
    product_id          VARCHAR(36)   NOT NULL REFERENCES account_product(product_id),
    tier_name           VARCHAR(100)  NOT NULL,
    tier_sequence       SMALLINT      NOT NULL,
    balance_from        NUMERIC(20,2) NOT NULL,
    balance_to          NUMERIC(20,2),
    credit_rate         NUMERIC(8,4)  NOT NULL DEFAULT 0,
    debit_rate          NUMERIC(8,4)  NOT NULL DEFAULT 0,
    rate_type           VARCHAR(10)   NOT NULL DEFAULT 'FIXED',   -- FIXED | FLOATING
    calculation_basis   VARCHAR(20)   NOT NULL DEFAULT 'DAILY_BALANCE',  -- DAILY_BALANCE | MINIMUM_IN_PERIOD | AVERAGE_DAILY
    effective_from      DATE          NOT NULL,
    effective_to        DATE,
    version             INTEGER       NOT NULL DEFAULT 1,
    CONSTRAINT chk_tier_balance CHECK (balance_from >= -99999999999999.99),
    UNIQUE(product_id, tier_sequence, effective_from)
);
CREATE INDEX idx_tier_product ON product_interest_tier(product_id, balance_from);
