package io.github.joaovictor.smartsupport.service;

import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamRequest;
import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamResponse;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.mapper.SupportTeamMapper;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SupportTeamService {

    private final SupportTeamRepository supportTeamRepository;
    private final SupportTeamMapper supportTeamMapper;

    @Transactional
    public SupportTeamResponse create(SupportTeamRequest request) {
        if (supportTeamRepository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma equipe com este nome");
        }
        SupportTeam supportTeam = supportTeamRepository.save(supportTeamMapper.toEntity(request));
        return supportTeamMapper.toResponse(supportTeam);
    }

    @Transactional(readOnly = true)
    public List<SupportTeamResponse> findAll() {
        return supportTeamRepository.findAll().stream()
                .map(supportTeamMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupportTeamResponse findById(UUID id) {
        return supportTeamMapper.toResponse(getSupportTeamOrThrow(id));
    }

    @Transactional
    public SupportTeamResponse update(UUID id, SupportTeamRequest request) {
        SupportTeam supportTeam = getSupportTeamOrThrow(id);
        if (!supportTeam.getName().equalsIgnoreCase(request.name()) && supportTeamRepository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma equipe com este nome");
        }
        supportTeamMapper.updateEntityFromRequest(request, supportTeam);
        return supportTeamMapper.toResponse(supportTeam);
    }

    @Transactional
    public void delete(UUID id) {
        supportTeamRepository.delete(getSupportTeamOrThrow(id));
    }

    private SupportTeam getSupportTeamOrThrow(UUID id) {
        return supportTeamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipe de suporte não encontrada"));
    }
}
