package io.github.joaovictor.smartsupport.command;

import io.github.joaovictor.smartsupport.dto.ticket.TicketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Invoker do padrão Command: ponto único de execução das ações de chamado.
 * Centralizar aqui permite instrumentar toda ação (log, e futuramente
 * auditoria/enfileiramento/undo) sem que os handlers conheçam esses detalhes.
 */
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
