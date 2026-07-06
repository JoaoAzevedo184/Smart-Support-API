package io.github.joaovictor.smartsupport.dto.ticket;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        TicketCategory category,
        UUID clientId,
        String clientName,
        UUID assignedTeamId,
        String assignedTeamName,
        UUID assignedUserId,
        String assignedUserName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt
) {
}
