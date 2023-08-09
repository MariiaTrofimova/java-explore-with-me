CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,

    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat FLOAT,
    lon FLOAT
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000)               NOT NULL,
    category_id        BIGINT                      NOT NULL,
    description        VARCHAR(7000)               NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location_id        BIGINT                      NOT NULL,
    paid               BOOLEAN                     NOT NULL,
    participant_limit  INT                         NOT NULL,
    request_moderation BOOLEAN                     NOT NULL,
    title              VARCHAR(120)                NOT NULL,
    initiator          BIGINT                      NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE default now(),
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    state              VARCHAR(9),

    CONSTRAINT fk_events_to_categories FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_locations FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_users FOREIGN KEY (initiator) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE default now(),
    status       VARCHAR(9),

    CONSTRAINT fk_requests_to_events FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title  VARCHAR(50) NOT NULL,
    pinned BOOLEAN     NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,

    CONSTRAINT fk_compilations_events_to_compilations
        FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    CONSTRAINT fk_compilations_events_to_events
        FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,

    PRIMARY KEY (compilation_id, event_id)
);