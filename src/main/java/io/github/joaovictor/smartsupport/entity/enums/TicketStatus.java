package io.github.joaovictor.smartsupport.entity.enums;

import java.util.Map;
import java.util.Set;

/**
 * Status do chamado e a máquina de estados que rege suas transições.
 * {@link #canTransitionTo} é a fonte única de verdade das transições válidas —
 * uma transição fora dessa tabela resulta em HTTP 409 na Facade.
 */
public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    REOPENED;

    // ===== Transições permitidas (máquina de estados) =====
    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = Map.of(
            OPEN, Set.of(IN_PROGRESS, CLOSED),
            IN_PROGRESS, Set.of(RESOLVED, CLOSED),
            RESOLVED, Set.of(CLOSED, REOPENED),
            CLOSED, Set.of(REOPENED),
            REOPENED, Set.of(IN_PROGRESS, CLOSED)
    );

    /** Verdadeiro se {@code target} é um destino válido a partir deste status. */
    public boolean canTransitionTo(TicketStatus target) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
