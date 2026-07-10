package io.github.joaovictor.smartsupport.dto.supportteam;

import java.time.LocalDateTime;
import java.util.UUID;

/** Representação de saída de uma equipe de suporte. */
public record SupportTeamResponse(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
