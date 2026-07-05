package io.github.joaovictor.smartsupport.dto.user;

import io.github.joaovictor.smartsupport.entity.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

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
