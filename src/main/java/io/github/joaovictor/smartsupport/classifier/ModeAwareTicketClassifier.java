package io.github.joaovictor.smartsupport.classifier;

public interface ModeAwareTicketClassifier extends TicketClassifier {

    ClassifierMode mode();
}
