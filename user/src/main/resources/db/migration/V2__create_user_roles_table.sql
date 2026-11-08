CREATE TABLE user_roles
(
    id         UUID PRIMARY KEY,
    user_id    UUID      NOT NULL,
    role_id    UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_user_roles_users
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT uniq_user_roles_user_id_and_role_id
        UNIQUE (user_id, role_id)
);

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);

CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);
