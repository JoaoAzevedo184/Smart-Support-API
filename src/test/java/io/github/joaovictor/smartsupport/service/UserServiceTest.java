package io.github.joaovictor.smartsupport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.joaovictor.smartsupport.dto.user.UserRequest;
import io.github.joaovictor.smartsupport.dto.user.UserResponse;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.entity.User;
import io.github.joaovictor.smartsupport.entity.enums.UserRole;
import io.github.joaovictor.smartsupport.mapper.UserMapper;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import io.github.joaovictor.smartsupport.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SupportTeamRepository supportTeamRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void deveCriarUsuarioSemEquipeQuandoSupportTeamIdNulo() {
        UserRequest request = new UserRequest("Agente", "agente@example.com", "senha1234", UserRole.AGENT, null);
        User entity = User.builder().email("agente@example.com").build();
        UserResponse response = new UserResponse(UUID.randomUUID(), "Agente", "agente@example.com", UserRole.AGENT, null, null, null, null);

        when(userRepository.existsByEmail("agente@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertThat(result).isEqualTo(response);
        assertThat(entity.getSupportTeam()).isNull();
    }

    @Test
    void deveLancar404QuandoSupportTeamInformadoNaoExiste() {
        UUID teamId = UUID.randomUUID();
        UserRequest request = new UserRequest("Agente", "agente@example.com", "senha1234", UserRole.AGENT, teamId);
        User entity = User.builder().email("agente@example.com").build();

        when(userRepository.existsByEmail("agente@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(supportTeamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Equipe de suporte não encontrada");
    }

    @Test
    void deveLancar409QuandoEmailJaExisteAoCriar() {
        UserRequest request = new UserRequest("Agente", "agente@example.com", "senha1234", UserRole.AGENT, null);
        when(userRepository.existsByEmail("agente@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Já existe");
    }

    @Test
    void deveAssociarEquipeQuandoSupportTeamIdInformado() {
        UUID teamId = UUID.randomUUID();
        UserRequest request = new UserRequest("Agente", "agente@example.com", "senha1234", UserRole.AGENT, teamId);
        User entity = User.builder().email("agente@example.com").build();
        SupportTeam team = SupportTeam.builder().id(teamId).name("Bug Team").build();
        UserResponse response = new UserResponse(UUID.randomUUID(), "Agente", "agente@example.com", UserRole.AGENT, teamId, "Bug Team", null, null);

        when(userRepository.existsByEmail("agente@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(supportTeamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertThat(entity.getSupportTeam()).isEqualTo(team);
        assertThat(result).isEqualTo(response);
    }
}
