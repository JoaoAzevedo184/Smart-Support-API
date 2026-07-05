package io.github.joaovictor.smartsupport.chain;

public abstract class TicketChainHandler {

    private TicketChainHandler next;

    public TicketChainHandler linkWith(TicketChainHandler next) {
        this.next = next;
        return next;
    }

    public final void handle(TicketProcessingContext context) {
        process(context);
        if (next != null) {
            next.handle(context);
        }
    }

    protected abstract void process(TicketProcessingContext context);
}
