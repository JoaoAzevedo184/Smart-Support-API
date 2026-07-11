package io.github.joaovictor.smartsupport.controller;

import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamRequest;
import io.github.joaovictor.smartsupport.dto.supportteam.SupportTeamResponse;
import io.github.joaovictor.smartsupport.service.SupportTeamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST de equipes de suporte (CRUD). Valida o DTO e delega ao
 * {@link SupportTeamService}.
 */
@RestController
@RequestMapping("/api/support-teams")
@RequiredArgsConstructor
@Tag(name = "Support Teams", description = "Gestão de equipes de suporte")
public class SupportTeamController {

    private final SupportTeamService supportTeamService;

    // ===== CRUD =====
    @PostMapping
    public ResponseEntity<SupportTeamResponse> create(@Valid @RequestBody SupportTeamRequest request) {
        SupportTeamResponse response = supportTeamService.create(request);
        return ResponseEntity.created(URI.create("/api/support-teams/" + response.id())).body(response);
    }

    @GetMapping
    public List<SupportTeamResponse> findAll() {
        return supportTeamService.findAll();
    }

    @GetMapping("/{id}")
    public SupportTeamResponse findById(@PathVariable UUID id) {
        return supportTeamService.findById(id);
    }

    @PutMapping("/{id}")
    public SupportTeamResponse update(@PathVariable UUID id, @Valid @RequestBody SupportTeamRequest request) {
        return supportTeamService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supportTeamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
