package io.github.joaovictor.smartsupport;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.joaovictor.smartsupport.config.TestcontainersConfig;
import io.github.joaovictor.smartsupport.dto.client.ClientRequest;
import io.github.joaovictor.smartsupport.dto.client.ClientResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketReportResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketRequest;
import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import io.github.joaovictor.smartsupport.dto.ticket.TicketStatusUpdateRequest;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
class TicketLifecycleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveAbrirMudarStatusEFecharTicketPontaAPonta() {
        ClientResponse client = restTemplate.postForObject(
                "/api/clients",
                new ClientRequest("Cliente Integração", "integracao@example.com", null),
                ClientResponse.class);

        TicketRequest ticketRequest = new TicketRequest(
                "Sistema apresentando falha",
                "Erro ao processar pagamentos desde ontem à noite",
                client.id(),
                TicketCategory.BUG,
                null);
        ResponseEntity<TicketResponse> createResponse = restTemplate.postForEntity(
                "/api/tickets", ticketRequest, TicketResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TicketResponse created = createResponse.getBody();
        assertThat(created.status()).isEqualTo(TicketStatus.OPEN);

        ResponseEntity<TicketResponse> statusResponse = restTemplate.exchange(
                "/api/tickets/" + created.id() + "/status",
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(new TicketStatusUpdateRequest(TicketStatus.IN_PROGRESS)),
                TicketResponse.class);
        assertThat(statusResponse.getBody().status()).isEqualTo(TicketStatus.IN_PROGRESS);

        ResponseEntity<TicketResponse> closeResponse = restTemplate.postForEntity(
                "/api/tickets/" + created.id() + "/close", null, TicketResponse.class);
        assertThat(closeResponse.getBody().status()).isEqualTo(TicketStatus.CLOSED);
        assertThat(closeResponse.getBody().closedAt()).isNotNull();

        ResponseEntity<TicketReportResponse> reportResponse = restTemplate.getForEntity(
                "/api/tickets/report", TicketReportResponse.class);
        assertThat(reportResponse.getBody().byStatus().get(TicketStatus.CLOSED)).isGreaterThanOrEqualTo(1L);
    }
}
