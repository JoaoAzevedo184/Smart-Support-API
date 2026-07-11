package io.github.joaovictor.smartsupport.dto.user;

import io.github.joaovictor.smartsupport.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** Dados de entrada para criar/atualizar um usuário. */
public record UserRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotNull
        UserRole role,

        UUID supportTeamId
) {
}
