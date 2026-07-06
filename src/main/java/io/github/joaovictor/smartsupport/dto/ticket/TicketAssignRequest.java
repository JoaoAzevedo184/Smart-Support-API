package io.github.joaovictor.smartsupport.dto.ticket;

import java.util.UUID;

public record TicketAssignRequest(
        UUID teamId,
        UUID userId
) {
}
