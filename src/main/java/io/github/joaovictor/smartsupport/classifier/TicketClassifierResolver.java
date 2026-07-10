package io.github.joaovictor.smartsupport.classifier;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Seleciona o {@link TicketClassifier} ativo conforme {@code app.classifier.strategy}.
 * Modo desconhecido ou ausente cai para {@link ClassifierMode#RULES}.
 */
@Component
public class TicketClassifierResolver {

    private final Map<ClassifierMode, ModeAwareTicketClassifier> classifiersByMode;
    private final ClassifierMode configuredMode;

    public TicketClassifierResolver(
            List<ModeAwareTicketClassifier> classifiers,
            @Value("${app.classifier.strategy:rules}") String configuredMode) {
        this.classifiersByMode = classifiers.stream()
                .collect(Collectors.toMap(ModeAwareTicketClassifier::mode, Function.identity()));
        this.configuredMode = parseMode(configuredMode);
    }

    public TicketClassifier getActive() {
        return classifiersByMode.getOrDefault(configuredMode, classifiersByMode.get(ClassifierMode.RULES));
    }

    private static ClassifierMode parseMode(String raw) {
        try {
            return ClassifierMode.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            return ClassifierMode.RULES;
        }
    }
}
