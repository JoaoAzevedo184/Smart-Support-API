package io.github.joaovictor.smartsupport.template;

import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BugTicketProcessor extends CategoryProcessor {

    private static final int MIN_DESCRIPTION_LENGTH_FOR_REPRO = 20;

    @Override
    public TicketCategory category() {
        return TicketCategory.BUG;
    }

    @Override
    protected void validate(Ticket ticket) {
        if (ticket.getDescription().trim().length() < MIN_DESCRIPTION_LENGTH_FOR_REPRO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A descrição de um chamado de bug deve detalhar os passos para reprodução");
        }
    }
}
