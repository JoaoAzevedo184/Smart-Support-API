CREATE TABLE clients (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phone       VARCHAR(30),
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_clients_email UNIQUE (email)
);
