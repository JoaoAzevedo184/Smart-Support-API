CREATE TABLE tickets (
    id               UUID PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT NOT NULL,
    status           VARCHAR(20) NOT NULL,
    priority         VARCHAR(20),
    category         VARCHAR(20),
    client_id        UUID NOT NULL,
    assigned_team_id UUID,
    assigned_user_id UUID,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP,
    closed_at        TIMESTAMP,
    CONSTRAINT fk_tickets_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_tickets_assigned_team FOREIGN KEY (assigned_team_id) REFERENCES support_teams (id),
    CONSTRAINT fk_tickets_assigned_user FOREIGN KEY (assigned_user_id) REFERENCES users (id)
);

CREATE INDEX idx_tickets_client_id ON tickets (client_id);
CREATE INDEX idx_tickets_assigned_team_id ON tickets (assigned_team_id);
CREATE INDEX idx_tickets_assigned_user_id ON tickets (assigned_user_id);
CREATE INDEX idx_tickets_status ON tickets (status);
