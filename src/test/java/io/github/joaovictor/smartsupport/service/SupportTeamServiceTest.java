package io.github.joaovictor.smartsupport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamRequest;
import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamResponse;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.mapper.SupportTeamMapper;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SupportTeamServiceTest {

    @Mock
    private SupportTeamRepository supportTeamRepository;
    @Mock
    private SupportTeamMapper supportTeamMapper;

    @InjectMocks
    private SupportTeamService supportTeamService;

    @Test
    void deveCriarEquipeQuandoNomeNaoExiste() {
        SupportTeamRequest request = new SupportTeamRequest("Bug Team", "Time de bugs");
        SupportTeam entity = SupportTeam.builder().name("Bug Team").build();
        SupportTeamResponse response = new SupportTeamResponse(UUID.randomUUID(), "Bug Team", "Time de bugs", null, null);

        when(supportTeamRepository.existsByName("Bug Team")).thenReturn(false);
        when(supportTeamMapper.toEntity(request)).thenReturn(entity);
        when(supportTeamRepository.save(entity)).thenReturn(entity);
        when(supportTeamMapper.toResponse(entity)).thenReturn(response);

        SupportTeamResponse result = supportTeamService.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void deveLancar409QuandoNomeJaExisteAoCriar() {
        SupportTeamRequest request = new SupportTeamRequest("Bug Team", "Time de bugs");
        when(supportTeamRepository.existsByName("Bug Team")).thenReturn(true);

        assertThatThrownBy(() -> supportTeamService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Já existe");
        verify(supportTeamRepository, never()).save(any());
    }

    @Test
    void deveLancar404QuandoEquipeNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(supportTeamRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supportTeamService.findById(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrada");
    }
}
