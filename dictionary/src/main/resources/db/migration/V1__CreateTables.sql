-- V1 Create tables lego_set, series

CREATE TABLE series
(
    name       VARCHAR(80) PRIMARY KEY,
    type       VARCHAR NOT NULL
        CHECK (type IN ('SYSTEM', 'TECHNIC')),
    complexity VARCHAR NOT NULL
        CHECK (complexity IN ('BEGINNER', 'ADVANCED', 'PROFESSIONAL')),
    age        INT
        CHECK (age >= 0)
);

CREATE TABLE lego_set
(
    number          VARCHAR(20) PRIMARY KEY,
    name            VARCHAR(120)  NOT NULL,
    age             INT
        CHECK (age > 0),
    parts_count     INT,
    suggested_price NUMERIC(8, 2) NOT NULL,
    series_id       VARCHAR(80)   NOT NULL
        CONSTRAINT fk_lego_set_series_id REFERENCES series (name)
);

CREATE UNIQUE INDEX udx_lego_set_name ON lego_set (name);
CREATE INDEX udx_lego_set_series_id ON lego_set (series_id);