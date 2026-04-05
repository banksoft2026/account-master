CREATE TABLE product_parameter (
    prod_param_id       VARCHAR(36)   PRIMARY KEY,
    product_id          VARCHAR(36)   NOT NULL REFERENCES account_product(product_id),
    param_key           VARCHAR(50)   NOT NULL,
    param_label         VARCHAR(100)  NOT NULL,
    value_type          VARCHAR(50)   NOT NULL,  -- DECIMAL | RATE | INTEGER | BOOLEAN | ENUM:val1,val2,...
    default_value       VARCHAR(100)  NOT NULL,
    min_value           VARCHAR(100),
    max_value           VARCHAR(100),
    overridable_at_account BOOLEAN    NOT NULL DEFAULT TRUE,
    mandatory           BOOLEAN       NOT NULL DEFAULT FALSE,
    effective_from      DATE          NOT NULL,
    effective_to        DATE,
    version             INTEGER       NOT NULL DEFAULT 1,
    UNIQUE(product_id, param_key, effective_from)
);
CREATE INDEX idx_param_product ON product_parameter(product_id);
CREATE INDEX idx_param_key     ON product_parameter(product_id, param_key);
