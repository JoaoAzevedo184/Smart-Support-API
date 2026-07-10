package io.github.joaovictor.smartsupport.dto.supportteam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dados de entrada para criar/atualizar uma equipe de suporte. */
public record SupportTeamRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description
) {
}
