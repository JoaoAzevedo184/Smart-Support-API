package io.github.joaovictor.smartsupport.chain;

import io.github.joaovictor.smartsupport.entity.Ticket;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SpamCheckHandler extends TicketChainHandler {

    private static final List<String> SPAM_KEYWORDS = List.of(
            "clique aqui", "ganhe dinheiro", "compre agora", "gratis", "http://", "https://", "www."
    );

    @Override
    protected void process(TicketProcessingContext context) {
        Ticket ticket = context.getTicket();
        String content = (ticket.getTitle() + " " + ticket.getDescription()).toLowerCase(Locale.ROOT);

        boolean isSpam = SPAM_KEYWORDS.stream().anyMatch(content::contains);
        if (isSpam) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chamado identificado como spam e não pode ser aberto");
        }
    }
}
