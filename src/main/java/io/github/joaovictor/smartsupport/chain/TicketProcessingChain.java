package io.github.joaovictor.smartsupport.chain;

import org.springframework.stereotype.Component;

/**
 * Monta e expõe a Chain of Responsibility na ordem
 * Validation → SpamCheck → Category → Priority → AssignTeam.
 * Inserir/remover uma etapa é mexer só aqui, sem tocar nos handlers.
 */
@Component
public class TicketProcessingChain {

    private final TicketChainHandler chain;

    // ===== Ligação dos elos (ordem do pipeline) =====
    public TicketProcessingChain(ValidationHandler validationHandler,
                                  SpamCheckHandler spamCheckHandler,
                                  CategoryHandler categoryHandler,
                                  PriorityHandler priorityHandler,
                                  AssignTeamHandler assignTeamHandler) {
        validationHandler.linkWith(spamCheckHandler)
                .linkWith(categoryHandler)
                .linkWith(priorityHandler)
                .linkWith(assignTeamHandler);
        this.chain = validationHandler;
    }

    // ===== Ponto de entrada =====
    public void process(TicketProcessingContext context) {
        chain.handle(context);
    }
}
