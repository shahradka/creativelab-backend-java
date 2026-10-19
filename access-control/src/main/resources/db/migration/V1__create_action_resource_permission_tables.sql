CREATE TABLE actions
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE resources
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE permissions
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    action_id   UUID       NOT NULL,
    resource_id UUID       NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,

    CONSTRAINT fk_permissions_actions
        FOREIGN KEY (action_id)
            REFERENCES actions (id),

    CONSTRAINT fk_permissions_resources
        FOREIGN KEY (resource_id)
            REFERENCES resources (id),

    CONSTRAINT uniq_action_id_and_resource_id
        UNIQUE (action_id, resource_id)
);
