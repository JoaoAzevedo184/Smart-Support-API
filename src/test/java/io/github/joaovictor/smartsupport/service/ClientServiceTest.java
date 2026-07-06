package io.github.joaovictor.smartsupport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.joaovictor.smartsupport.dto.client.ClientRequest;
import io.github.joaovictor.smartsupport.dto.client.ClientResponse;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.mapper.ClientMapper;
import io.github.joaovictor.smartsupport.repository.ClientRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    @Test
    void deveCriarClienteQuandoEmailNaoExiste() {
        ClientRequest request = new ClientRequest("Cliente", "cliente@example.com", null);
        Client entity = Client.builder().email("cliente@example.com").build();
        ClientResponse response = new ClientResponse(UUID.randomUUID(), "Cliente", "cliente@example.com", null, null, null);

        when(clientRepository.existsByEmail("cliente@example.com")).thenReturn(false);
        when(clientMapper.toEntity(request)).thenReturn(entity);
        when(clientRepository.save(entity)).thenReturn(entity);
        when(clientMapper.toResponse(entity)).thenReturn(response);

        ClientResponse result = clientService.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void deveLancar409QuandoEmailJaExisteAoCriar() {
        ClientRequest request = new ClientRequest("Cliente", "cliente@example.com", null);
        when(clientRepository.existsByEmail("cliente@example.com")).thenReturn(true);

        assertThatThrownBy(() -> clientService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Já existe");
        verify(clientRepository, never()).save(any());
    }

    @Test
    void deveLancar404QuandoClienteNaoEncontradoPorId() {
        UUID id = UUID.randomUUID();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void deveExcluirClienteExistente() {
        UUID id = UUID.randomUUID();
        Client client = Client.builder().id(id).build();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));

        clientService.delete(id);

        verify(clientRepository).delete(client);
    }
}
