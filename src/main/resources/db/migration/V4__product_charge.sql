CREATE TABLE product_charge (
    charge_id           VARCHAR(36)   PRIMARY KEY,
    product_id          VARCHAR(36)   NOT NULL REFERENCES account_product(product_id),
    charge_code         VARCHAR(30)   NOT NULL,
    charge_name         VARCHAR(150)  NOT NULL,
    charge_type         VARCHAR(30)   NOT NULL,  -- MAINTENANCE | TRANSACTION | PENALTY | DORMANCY | PAPER_STATEMENT | CLOSURE
    charge_amount       NUMERIC(20,2),
    charge_currency     VARCHAR(3)    NOT NULL,
    percentage_rate     NUMERIC(8,4),
    frequency           VARCHAR(20)   NOT NULL,  -- MONTHLY | QUARTERLY | YEARLY | PER_TRANSACTION | ON_EVENT
    trigger_event       VARCHAR(50),
    waivable            BOOLEAN       NOT NULL DEFAULT FALSE,
    min_balance_for_waiver NUMERIC(20,2),
    is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
    effective_from      DATE          NOT NULL,
    effective_to        DATE,
    version             INTEGER       NOT NULL DEFAULT 1
);
CREATE INDEX idx_charge_product ON product_charge(product_id);
CREATE INDEX idx_charge_active  ON product_charge(product_id) WHERE is_active = TRUE;
