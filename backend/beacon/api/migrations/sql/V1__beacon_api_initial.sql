CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

CREATE SCHEMA IF NOT EXISTS beacon;

CREATE TABLE IF NOT EXISTS beacon.beacon
(
    sequence  smallint NOT NULL,
    timestamp integer  NOT NULL,
    events    jsonb    NOT NULL
);