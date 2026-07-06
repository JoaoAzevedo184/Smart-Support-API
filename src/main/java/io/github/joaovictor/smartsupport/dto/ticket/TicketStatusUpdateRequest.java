package io.github.joaovictor.smartsupport.dto.ticket;

import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TicketStatusUpdateRequest(

        @NotNull
        TicketStatus status
) {
}
