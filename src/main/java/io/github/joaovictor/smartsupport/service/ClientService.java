package io.github.joaovictor.smartsupport.service;

import io.github.joaovictor.smartsupport.dto.client.ClientRequest;
import io.github.joaovictor.smartsupport.dto.client.ClientResponse;
import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.mapper.ClientMapper;
import io.github.joaovictor.smartsupport.repository.ClientRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Regra de negócio de clientes (CRUD). Bean singleton gerenciado pelo Spring;
 * garante unicidade de e-mail e traduz entidade ↔ DTO via {@link ClientMapper}.
 */
@Service
@RequiredArgsConstructor
public class ClientService {

    // ===== Dependências =====
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    // ===== Operações de escrita =====
    @Transactional
    public ClientResponse create(ClientRequest request) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com este e-mail");
        }
        Client client = clientRepository.save(clientMapper.toEntity(request));
        return clientMapper.toResponse(client);
    }

    // ===== Consultas (somente leitura) =====
    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(UUID id) {
        return clientMapper.toResponse(getClientOrThrow(id));
    }

    @Transactional
    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = getClientOrThrow(id);
        if (!client.getEmail().equalsIgnoreCase(request.email()) && clientRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com este e-mail");
        }
        clientMapper.updateEntityFromRequest(request, client);
        return clientMapper.toResponse(client);
    }

    @Transactional
    public void delete(UUID id) {
        clientRepository.delete(getClientOrThrow(id));
    }

    // ===== Apoio =====
    private Client getClientOrThrow(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }
}
