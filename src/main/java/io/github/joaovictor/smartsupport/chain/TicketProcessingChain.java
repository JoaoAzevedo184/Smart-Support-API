package io.github.joaovictor.smartsupport.chain;

import org.springframework.stereotype.Component;

@Component
public class TicketProcessingChain {

    private final TicketChainHandler chain;

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

    public void process(TicketProcessingContext context) {
        chain.handle(context);
    }
}
