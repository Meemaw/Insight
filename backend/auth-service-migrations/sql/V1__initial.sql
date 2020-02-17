CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.org
(
    id         TEXT        NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);

ALTER TABLE auth.org
    DROP CONSTRAINT IF EXISTS org_id_len;
ALTER TABLE auth.org
    ADD CONSTRAINT org_id_len CHECK (length(auth.org.id) = 6);

CREATE TABLE IF NOT EXISTS auth.user
(
    id         UUID        NOT NULL UNIQUE DEFAULT uuid_generate_v4(),
    email      TEXT        NOT NULL UNIQUE,
    org        TEXT        NOT NULL,
    role       TEXT        NOT NULL,
    first_name TEXT,
    last_name  TEXT,
    phone      TEXT,
    created_at TIMESTAMPTZ NOT NULL        DEFAULT now(),

    PRIMARY KEY (id, email, org),
    FOREIGN KEY (org) REFERENCES auth.org (id)
);

ALTER TABLE auth.user
    DROP CONSTRAINT IF EXISTS user_email_len;
ALTER TABLE auth.user
    ADD CONSTRAINT user_email_len CHECK (length(auth.user.email) < 255);

CREATE TABLE IF NOT EXISTS auth.password
(
    user_id    UUID        NOT NULL,
    hash       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (user_id, hash),
    FOREIGN KEY (user_id) REFERENCES auth.user (id)
);

CREATE INDEX IF NOT EXISTS user_password_ix ON auth.password (user_id, created_at);

CREATE TABLE IF NOT EXISTS auth.signup
(
    token      UUID        NOT NULL UNIQUE DEFAULT uuid_generate_v4(),
    user_email TEXT        NOT NULL,
    user_id    UUID        NOT NULL,
    org        TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL        DEFAULT now(),

    PRIMARY KEY (token, user_id, user_email, org),
    FOREIGN KEY (org) REFERENCES auth.org (id),
    FOREIGN KEY (user_email) REFERENCES auth.user (email),
    FOREIGN KEY (user_id) REFERENCES auth.user (id)
);

ALTER TABLE auth.signup
    DROP CONSTRAINT IF EXISTS signup_email_len;

ALTER TABLE auth.signup
    ADD CONSTRAINT signup_email_len CHECK (length(auth.signup.user_email) < 255);

CREATE TABLE IF NOT EXISTS auth.invite
(
    token      UUID        NOT NULL UNIQUE DEFAULT uuid_generate_v4(),
    creator    UUID        NOT NULL,
    user_email TEXT        NOT NULL,
    org        TEXT        NOT NULL,
    role       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL        DEFAULT now(),

    PRIMARY KEY (token),
    FOREIGN KEY (org) REFERENCES auth.org (id),
    FOREIGN KEY (creator) REFERENCES auth.user (id),
    CONSTRAINT no_duplicates UNIQUE (org, user_email)
);

ALTER TABLE auth.invite
    DROP CONSTRAINT IF EXISTS invitation_email_len;

ALTER TABLE auth.invite
    ADD CONSTRAINT invitation_email_len CHECK (length(auth.invite.user_email) < 255);