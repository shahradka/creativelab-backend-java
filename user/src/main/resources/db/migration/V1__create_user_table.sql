CREATE TABLE users
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    users_passwords_id BIGINT       NOT NULL,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL,

    CONSTRAINT fk_users_ups FOREIGN KEY (users_passwords_id) REFERENCES users_passwords (id)
);

CREATE TABLE users_passwords
(
    id          BIGSERIAL PRIMARY KEY,
    passwords_id BIGINT NOT NULL,
    users_id     BIGINT NOT NULL,
    CONSTRAINT fk_ups_passwords FOREIGN KEY (passwords_id) REFERENCES passwords (id),
    CONSTRAINT fk_ups_users FOREIGN KEY (users_id) REFERENCES users(id)
);

CREATE TABLE passwords
(
    id         BIGSERIAL PRIMARY KEY,
    hashed      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
)