CREATE TABLE IF NOT EXISTS hit (
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    app               VARCHAR(128) NOT NULL,
    uri               VARCHAR(512) NOT NULL,
    ip                VARCHAR(64)  NOT NULL,
    timestamp         TIMESTAMP    WITHOUT TIME ZONE     NOT NULL,
    CONSTRAINT        PK_HIT       PRIMARY KEY(id)
);