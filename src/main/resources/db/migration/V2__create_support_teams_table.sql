CREATE TABLE support_teams (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_support_teams_name UNIQUE (name)
);
