package io.github.joaovictor.smartsupport.dto.user;

import io.github.joaovictor.smartsupport.entity.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

/** Representação de saída de um usuário (sem a senha). */
public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        UUID supportTeamId,
        String supportTeamName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
