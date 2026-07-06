package io.github.joaovictor.smartsupport.controller;

import io.github.joaovictor.smartsupport.dto.ticket.TicketAssignRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketStatusUpdateRequest;
import io.github.joaovictor.smartsupport.facade.TicketFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Abertura e gestão de chamados")
public class TicketController {

    private final TicketFacade ticketFacade;

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
        return ResponseEntity.ok(ticketFacade.assign(id, request));
    }
}
