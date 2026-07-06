package io.github.joaovictor.smartsupport.command;

import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketCommandInvoker {

    public TicketResponse run(TicketCommand command) {
        String commandName = command.getClass().getSimpleName();
        log.info("[COMMAND] Executando {}", commandName);
        TicketResponse response = command.execute();
        log.info("[COMMAND] {} concluído para o chamado {}", commandName, response.id());
        return response;
    }
}
