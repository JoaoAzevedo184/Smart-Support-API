package io.github.joaovictor.smartsupport.classifier;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;

public interface TicketClassifier {

    TicketCategory classify(String title, String description);
}
