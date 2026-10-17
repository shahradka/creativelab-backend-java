CREATE TABLE actions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
)

CREATE TABLE resources
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
)

CREATE TABLE permissions
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    action_id   BIGSERIAL    NOT NULL,
    resource_id BIGSERIAL    NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,

    CONSTRAINT fk_permissions_actions
        FOREIGN KEY (action_id),
    REFERENCES  actions(id),

    CONSTRAINT fk_permissions_resources
        FOREIGN KEY (resource_id),
    REFERENCES  resources(id),

    CONSTRAINT uniq_action_id_and_resource_id
        UNIQUE (action_id, resource_id),
)
