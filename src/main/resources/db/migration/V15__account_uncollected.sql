CREATE TABLE account_uncollected (
    uncollected_id          VARCHAR(36)    PRIMARY KEY,
    account_id              VARCHAR(36)    NOT NULL REFERENCES account_master(account_id),
    instrument_type         VARCHAR(20)    NOT NULL,
    instrument_ref          VARCHAR(100)   NOT NULL,
    presenting_bank         VARCHAR(100),
    presenting_bank_bic     VARCHAR(11),
    instrument_amount       NUMERIC(20,2)  NOT NULL,
    currency_code           VARCHAR(3)     NOT NULL,
    collection_status       VARCHAR(20)    NOT NULL DEFAULT 'PRESENTED',
    presented_date          DATE           NOT NULL,
    expected_clearance_date DATE           NOT NULL,
    actual_clearance_date   DATE,
    cleared_amount          NUMERIC(20,2)  NOT NULL DEFAULT 0,
    returned_amount         NUMERIC(20,2)  NOT NULL DEFAULT 0,
    return_reason           VARCHAR(200),
    clearing_ref            VARCHAR(100),
    source_txn_id           VARCHAR(36),
    cleared_txn_id          VARCHAR(36),
    created_by              VARCHAR(36)    NOT NULL,
    created_at              TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP,
    version                 INTEGER        NOT NULL DEFAULT 1,
    CONSTRAINT chk_uncoll_amount CHECK (instrument_amount > 0)
);
CREATE INDEX idx_uncoll_account   ON account_uncollected(account_id, collection_status);
CREATE INDEX idx_uncoll_pending   ON account_uncollected(account_id) WHERE collection_status IN ('PRESENTED','IN_CLEARING');
CREATE INDEX idx_uncoll_clearance ON account_uncollected(expected_clearance_date) WHERE collection_status IN ('PRESENTED','IN_CLEARING');
