CREATE TABLE users
(
    id          UUID PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    user_status VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    firstname   VARCHAR(255),
    lastname    VARCHAR(255),
    email       VARCHAR(255),
    birthday    DATE,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);
