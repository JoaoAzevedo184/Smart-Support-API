package io.github.joaovictor.smartsupport.dto.ticket;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import java.util.Map;

public record TicketReportResponse(
        long totalTickets,
        Map<TicketStatus, Long> byStatus,
        Map<TicketCategory, Long> byCategory,
        Map<TicketPriority, Long> byPriority
) {
}
