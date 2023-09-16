CREATE TABLE IF NOT EXISTS users (
    id                BIGINT         NOT NULL GENERATED ALWAYS AS IDENTITY,
    email             VARCHAR(254)   NOT NULL,
    first_name        VARCHAR(250)   NOT NULL,
    CONSTRAINT        PK_USER        PRIMARY KEY(id),
    CONSTRAINT        UNQ_USER_EMAIL UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS category (
    id                INTEGER           NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(50)       NOT NULL,
    CONSTRAINT        PK_CATEGORY       PRIMARY KEY(id),
    CONSTRAINT        UNQ_CATEGORY_NAME UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS location (
    id                BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    lat               FLOAT         NOT NULL,
    lon               FLOAT         NOT NULL,
    CONSTRAINT        PK_LOCATION   PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS event (
    id                  BIGINT               NOT NULL GENERATED ALWAYS AS IDENTITY,
    annotation          VARCHAR(2000)        NOT NULL,
    category_id         INTEGER              NOT NULL,
    confirmed_requests  INTEGER              DEFAULT 0,
    created_on          TIMESTAMP            WITHOUT TIME ZONE     NOT NULL,
    description         VARCHAR(7000)        NOT NULL,
    event_date          TIMESTAMP            WITHOUT TIME ZONE     NOT NULL,
    initiator_id        BIGINT               NOT NULL,
    location_id         BIGINT               NOT NULL,
    paid                BOOLEAN              NOT NULL,
    participant_limit   INTEGER              DEFAULT 0,
    published_on        TIMESTAMP            WITHOUT TIME ZONE,
    request_moderation  BOOLEAN              NOT NULL,
    state               VARCHAR(32)          NOT NULL,
    title               VARCHAR(120)         NOT NULL,
    views               INTEGER              DEFAULT 0,
    CONSTRAINT          PK_EVENT             PRIMARY KEY(id),
    CONSTRAINT          FK_EVENT_TO_CATEGORY FOREIGN KEY(category_id)  REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT          FK_EVENT_TO_LOCATION FOREIGN KEY(location_id)  REFERENCES location(id) ON DELETE CASCADE,
    CONSTRAINT          FK_EVENT_TO_USER     FOREIGN KEY(initiator_id) REFERENCES users(id)    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS request (
    id                  BIGINT                 NOT NULL GENERATED ALWAYS AS IDENTITY,
    created             TIMESTAMP              WITHOUT TIME ZONE     NOT NULL,
    event_id            BIGINT                 NOT NULL,
    requester_id        BIGINT                 NOT NULL,
    status              VARCHAR(32)            NOT NULL,
    CONSTRAINT          PK_REQUEST             PRIMARY KEY(id),
    CONSTRAINT          FK_REQUEST_TO_EVENT    FOREIGN KEY(requester_id) REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT          FK_REQUEST_TO_CATEGORY FOREIGN KEY(event_id)  REFERENCES event(id) ON DELETE CASCADE

);