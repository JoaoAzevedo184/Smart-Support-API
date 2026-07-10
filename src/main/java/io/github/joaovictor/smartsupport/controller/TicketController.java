package io.github.joaovictor.smartsupport.controller;

import io.github.joaovictor.smartsupport.command.AssignTicketCommand;
import io.github.joaovictor.smartsupport.command.CloseTicketCommand;
import io.github.joaovictor.smartsupport.command.ReopenTicketCommand;
import io.github.joaovictor.smartsupport.command.TicketCommandInvoker;
import io.github.joaovictor.smartsupport.dto.ticket.TicketAssignRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketReportResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketStatusUpdateRequest;
import io.github.joaovictor.smartsupport.facade.TicketFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST de chamados. Delega a abertura e as consultas à
 * {@link TicketFacade}, e as ações (atribuir/fechar/reabrir) ao
 * {@link TicketCommandInvoker} (padrão Command).
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Abertura e gestão de chamados")
public class TicketController {

    // ===== Dependências =====
    private final TicketFacade ticketFacade;
    private final TicketCommandInvoker ticketCommandInvoker;

    // ===== Abertura e ciclo de vida =====
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketFacade.openTicket(request);
        return ResponseEntity.created(URI.create("/api/tickets/" + response.id())).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody TicketStatusUpdateRequest request) {
        return ResponseEntity.ok(ticketFacade.changeStatus(id, request));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assign(
            @PathVariable UUID id,
            @RequestBody TicketAssignRequest request) {
        return ResponseEntity.ok(ticketCommandInvoker.run(new AssignTicketCommand(ticketFacade, id, request)));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<TicketResponse> close(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketCommandInvoker.run(new CloseTicketCommand(ticketFacade, id)));
    }

    @PostMapping("/{id}/reopen")
    public ResponseEntity<TicketResponse> reopen(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketCommandInvoker.run(new ReopenTicketCommand(ticketFacade, id)));
    }

    // ===== Consultas =====
    @GetMapping("/open")
    public ResponseEntity<List<TicketResponse>> listOpen() {
        return ResponseEntity.ok(ticketFacade.listOpenTickets());
    }

    @GetMapping("/report")
    public ResponseEntity<TicketReportResponse> report() {
        return ResponseEntity.ok(ticketFacade.generateReport());
    }
}
