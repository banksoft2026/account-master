CREATE TABLE account_daily_position (
    position_id      VARCHAR(36)    PRIMARY KEY,
    account_id       VARCHAR(36)    NOT NULL REFERENCES account_master(account_id),
    currency_code    VARCHAR(3)     NOT NULL,
    position_date    DATE           NOT NULL,
    opening_balance  NUMERIC(20,2)  NOT NULL DEFAULT 0,
    total_credits    NUMERIC(20,2)  NOT NULL DEFAULT 0,
    total_debits     NUMERIC(20,2)  NOT NULL DEFAULT 0,
    credit_count     INTEGER        NOT NULL DEFAULT 0,
    debit_count      INTEGER        NOT NULL DEFAULT 0,
    peak_balance     NUMERIC(20,2),
    trough_balance   NUMERIC(20,2),
    average_balance  NUMERIC(20,2),
    eod_confirmed    BOOLEAN        NOT NULL DEFAULT FALSE,
    eod_confirmed_at TIMESTAMP,
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP,
    version          INTEGER        NOT NULL DEFAULT 1,
    UNIQUE (account_id, position_date, currency_code)
);
CREATE INDEX idx_daily_pos_account ON account_daily_position(account_id, position_date DESC);
CREATE INDEX idx_daily_pos_open    ON account_daily_position(position_date) WHERE eod_confirmed = FALSE;
