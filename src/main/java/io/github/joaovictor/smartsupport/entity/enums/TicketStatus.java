package io.github.joaovictor.smartsupport.entity.enums;

import java.util.Map;
import java.util.Set;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    REOPENED;

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = Map.of(
            OPEN, Set.of(IN_PROGRESS, CLOSED),
            IN_PROGRESS, Set.of(RESOLVED, CLOSED),
            RESOLVED, Set.of(CLOSED, REOPENED),
            CLOSED, Set.of(REOPENED),
            REOPENED, Set.of(IN_PROGRESS, CLOSED)
    );

    public boolean canTransitionTo(TicketStatus target) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
