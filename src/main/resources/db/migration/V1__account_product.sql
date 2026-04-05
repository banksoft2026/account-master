CREATE TABLE account_product (
    product_id          VARCHAR(36)   PRIMARY KEY,
    product_code        VARCHAR(30)   NOT NULL UNIQUE,
    product_name        VARCHAR(200)  NOT NULL,
    account_type        VARCHAR(20)   NOT NULL,  -- CURRENT | SAVINGS | TERM_DEPOSIT | LOAN | OVERDRAFT
    target_segment      VARCHAR(30)   NOT NULL,  -- CORPORATE | RETAIL | SME | HNW | GOVERNMENT
    currency_code       VARCHAR(3)    NOT NULL,
    description         VARCHAR(500),
    allow_joint         BOOLEAN       NOT NULL DEFAULT FALSE,
    allow_corporate     BOOLEAN       NOT NULL DEFAULT TRUE,
    allow_individual    BOOLEAN       NOT NULL DEFAULT FALSE,
    min_opening_balance NUMERIC(20,2) NOT NULL DEFAULT 0,
    max_balance         NUMERIC(20,2),
    max_account_per_customer SMALLINT,
    lifecycle_status    VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',  -- DRAFT | ACTIVE | SUSPENDED | RETIRED
    effective_from      DATE          NOT NULL,
    effective_to        DATE,
    created_by          VARCHAR(36)   NOT NULL,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by          VARCHAR(36),
    updated_at          TIMESTAMP,
    version             INTEGER       NOT NULL DEFAULT 1
);
CREATE INDEX idx_product_type     ON account_product(account_type, lifecycle_status);
CREATE INDEX idx_product_segment  ON account_product(target_segment);
CREATE INDEX idx_product_currency ON account_product(currency_code);
CREATE INDEX idx_product_active   ON account_product(product_code) WHERE lifecycle_status = 'ACTIVE';
