package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.entity.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * 1º elo do pipeline: valida tamanho mínimo de título e descrição.
 * Falha aqui interrompe a cadeia com HTTP 400.
 */
@Component
public class ValidationHandler extends TicketChainHandler {

    private static final int MIN_TITLE_LENGTH = 5;
    private static final int MIN_DESCRIPTION_LENGTH = 10;

    @Override
    protected void process(TicketProcessingContext context) {
        Ticket ticket = context.getTicket();

        if (ticket.getTitle().trim().length() < MIN_TITLE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O título do chamado deve ter ao menos " + MIN_TITLE_LENGTH + " caracteres");
        }
        if (ticket.getDescription().trim().length() < MIN_DESCRIPTION_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A descrição do chamado deve ter ao menos " + MIN_DESCRIPTION_LENGTH + " caracteres");
        }
    }
}
