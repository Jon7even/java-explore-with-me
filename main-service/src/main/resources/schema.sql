CREATE TABLE IF NOT EXISTS users (
    id                BIGINT          NOT NULL GENERATED ALWAYS AS IDENTITY,
    email             VARCHAR(254)    NOT NULL,
    first_name        VARCHAR(250)    NOT NULL,
    CONSTRAINT        PK_USER         PRIMARY KEY(id),
    CONSTRAINT        UNQ_USER_EMAIL  UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS category (
    id                INTEGER            NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(50)        NOT NULL,
    CONSTRAINT        PK_CATEGORY        PRIMARY KEY(id),
    CONSTRAINT        UNQ_CATEGORY_NAME  UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS location (
    id                BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    lat               FLOAT         NOT NULL,
    lon               FLOAT         NOT NULL,
    CONSTRAINT        PK_LOCATION   PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS event (
    id                  BIGINT                NOT NULL GENERATED ALWAYS AS IDENTITY,
    annotation          VARCHAR(2000)         NOT NULL,
    category_id         INTEGER               NOT NULL,
    confirmed_requests  INTEGER               DEFAULT 0,
    created_on          TIMESTAMP             WITHOUT TIME ZONE     NOT NULL,
    description         VARCHAR(7000)         NOT NULL,
    event_date          TIMESTAMP             WITHOUT TIME ZONE     NOT NULL,
    initiator_id        BIGINT                NOT NULL,
    location_id         BIGINT                NOT NULL,
    paid                BOOLEAN               NOT NULL,
    participant_limit   INTEGER               DEFAULT 0,
    published_on        TIMESTAMP             WITHOUT TIME ZONE,
    request_moderation  BOOLEAN               NOT NULL,
    state               VARCHAR(32)           NOT NULL,
    title               VARCHAR(120)          NOT NULL,
    views               INTEGER               DEFAULT 0,
    CONSTRAINT          PK_EVENT              PRIMARY KEY(id),
    CONSTRAINT          FK_EVENT_TO_CATEGORY  FOREIGN KEY(category_id)  REFERENCES category(id)   ON DELETE CASCADE,
    CONSTRAINT          FK_EVENT_TO_LOCATION  FOREIGN KEY(location_id)  REFERENCES location(id)   ON DELETE CASCADE,
    CONSTRAINT          FK_EVENT_TO_USER      FOREIGN KEY(initiator_id) REFERENCES users(id)      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS request (
    id                  BIGINT                  NOT NULL GENERATED ALWAYS AS IDENTITY,
    created             TIMESTAMP               WITHOUT TIME ZONE     NOT NULL,
    event_id            BIGINT                  NOT NULL,
    requester_id        BIGINT                  NOT NULL,
    status              VARCHAR(32)             NOT NULL,
    CONSTRAINT          PK_REQUEST              PRIMARY KEY(id),
    CONSTRAINT          FK_REQUEST_TO_USER      FOREIGN KEY(requester_id) REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT          FK_REQUEST_TO_EVENT     FOREIGN KEY(event_id)     REFERENCES event(id)   ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilation (
    id                INTEGER                 NOT NULL GENERATED ALWAYS AS IDENTITY,
    pinned            BOOLEAN                 DEFAULT FALSE,
    title             VARCHAR(50)             NOT NULL,
    CONSTRAINT        PK_COMPILATION          PRIMARY KEY(id),
    CONSTRAINT        UNQ_COMPILATION_TITLE   UNIQUE(title)
);

CREATE TABLE IF NOT EXISTS compilation_event (
    compilation_id    INTEGER         NOT NULL,
    event_id          BIGINT          NOT NULL,
    CONSTRAINT        FK_COMPILATION_EVENT_TO_COMPILATION  FOREIGN KEY(compilation_id) REFERENCES compilation(id)  ON DELETE CASCADE,
    CONSTRAINT        FK_COMPILATION_EVENT_TO_EVENT        FOREIGN KEY(event_id)       REFERENCES event(id)        ON DELETE CASCADE,
    CONSTRAINT        PK_COMPILATION_EVENT                 PRIMARY KEY(compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS event_rating (
    liker_id          BIGINT                  NOT NULL,
    event_id          BIGINT                  NOT NULL,
    is_positive       BOOLEAN                 NOT NULL,
    created_on        TIMESTAMP               WITHOUT TIME ZONE     NOT NULL,
    updated_on        TIMESTAMP               WITHOUT TIME ZONE,
    CONSTRAINT        PK_RATING               PRIMARY KEY(liker_id, event_id),
    CONSTRAINT        FK_RATING_TO_USERS      FOREIGN KEY(liker_id)   REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT        FK_RATING_TO_EVENT      FOREIGN KEY(event_id)   REFERENCES event(id)   ON DELETE CASCADE
);

