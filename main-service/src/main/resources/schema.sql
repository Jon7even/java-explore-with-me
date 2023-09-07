CREATE TABLE IF NOT EXISTS users (
    id                BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    email             VARCHAR(254)  NOT NULL,
    first_name        VARCHAR(250)  NOT NULL,
    CONSTRAINT        PK_USER PRIMARY KEY(id),
    CONSTRAINT        UNQ_USER_EMAIL UNIQUE(email)
);
