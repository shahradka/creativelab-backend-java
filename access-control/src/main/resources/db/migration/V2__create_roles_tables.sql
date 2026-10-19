CREATE TABLE roles_permissions
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    permissions_id UUID    NOT NULL,
    roles_id       UUID    NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP NOT NULL,

    CONSTRAINT fk_rp_permissions
        FOREIGN KEY (permissions_id)
            REFERENCES permissions(id),

    CONSTRAINT uniq_roles_id_and_permissions_id
        UNIQUE (permissions_id, roles_id)
);

CREATE TABLE roles
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                 VARCHAR(255) NOT NULL,
    permissions_roles_id UUID,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,

    CONSTRAINT fk_permissions_roles
        FOREIGN KEY (permissions_roles_id)
            REFERENCES roles_permissions(id)
);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rp_roles
        FOREIGN KEY (roles_id)
            REFERENCES roles(id);