package io.github.joaovictor.smartsupport.dto.ticket;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** Dados de entrada para abrir um chamado (categoria e prioridade opcionais). */
public record TicketRequest(

        @NotBlank
        @Size(max = 255)
        String title,

        @NotBlank
        String description,

        @NotNull
        UUID clientId,

        // opcional: quando omitida, o CategoryHandler classifica a partir do título/descrição
        TicketCategory category,

        TicketPriority priority
) {
}
