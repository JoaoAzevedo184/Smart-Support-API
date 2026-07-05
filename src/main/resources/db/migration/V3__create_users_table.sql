CREATE TABLE users (
    id              UUID PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(20) NOT NULL,
    support_team_id UUID,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_support_team FOREIGN KEY (support_team_id) REFERENCES support_teams (id)
);

CREATE INDEX idx_users_support_team_id ON users (support_team_id);
