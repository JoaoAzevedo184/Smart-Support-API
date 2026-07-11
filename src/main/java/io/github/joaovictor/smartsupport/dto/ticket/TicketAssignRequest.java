package io.github.joaovictor.smartsupport.dto.ticket;

import java.util.UUID;

/** Dados de atribuição de um chamado (equipe e/ou usuário; ao menos um). */
public record TicketAssignRequest(
        UUID teamId,
        UUID userId
) {
}
