package io.github.joaovictor.smartsupport.dto.ticket;

import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

/** Dados para atualizar o status de um chamado. */
public record TicketStatusUpdateRequest(

        @NotNull
        TicketStatus status
) {
}
