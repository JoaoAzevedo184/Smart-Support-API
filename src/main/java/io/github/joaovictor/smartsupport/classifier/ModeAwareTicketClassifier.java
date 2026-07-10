package io.github.joaovictor.smartsupport.classifier;

/**
 * {@link TicketClassifier} que declara seu {@link ClassifierMode}, permitindo ao
 * {@link TicketClassifierResolver} indexá-lo e selecioná-lo por configuração.
 */
public interface ModeAwareTicketClassifier extends TicketClassifier {

    ClassifierMode mode();
}
