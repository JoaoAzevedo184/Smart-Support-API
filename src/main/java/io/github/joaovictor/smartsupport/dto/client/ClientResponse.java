package io.github.joaovictor.smartsupport.dto.client;

import java.time.LocalDateTime;
import java.util.UUID;

/** Representação de saída de um cliente. */
public record ClientResponse(
        UUID id,
        String name,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
