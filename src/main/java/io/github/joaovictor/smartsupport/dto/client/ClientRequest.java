package io.github.joaovictor.smartsupport.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dados de entrada para criar/atualizar um cliente. */
public record ClientRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Size(max = 20)
        String phone
) {
}
