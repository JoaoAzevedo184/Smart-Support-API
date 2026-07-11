package io.github.joaovictor.smartsupport.classifier;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Classifica via LLM (modelo ativo por {@code spring.ai.model.chat}: Ollama por padrão,
 * Gemini no perfil {@code gemini}). Não conhece o provedor concreto — programa contra
 * {@link ChatClient}, que o Spring AI monta a partir do {@code ChatModel} configurado.
 * Qualquer falha (timeout, resposta não reconhecida, IA indisponível) cai para
 * {@link RuleBasedClassifier}, sem interromper a criação do chamado.
 */
@Slf4j
@Component
public class AiTicketClassifier implements ModeAwareTicketClassifier {

    private static final String PROMPT_TEMPLATE = """
            Classifique o chamado de suporte abaixo em exatamente uma categoria: BUG, BILLING ou SUPPORT.
            BUG: erro, falha ou comportamento inesperado do sistema.
            BILLING: cobrança, fatura, pagamento ou reembolso.
            SUPPORT: dúvidas ou pedidos gerais que não se encaixam nas anteriores.
            Responda apenas com a palavra da categoria, sem explicações.

            Título: %s
            Descrição: %s
            """;

    // ===== Colaboradores (LLM + fallback determinístico) =====
    private final ChatClient chatClient;
    private final RuleBasedClassifier fallback;

    public AiTicketClassifier(ChatClient.Builder chatClientBuilder, RuleBasedClassifier fallback) {
        this.chatClient = chatClientBuilder.build();
        this.fallback = fallback;
    }

    @Override
    public ClassifierMode mode() {
        return ClassifierMode.AI;
    }

    // ===== Classificação (chama o LLM; em qualquer falha, cai para regras) =====
    @Override
    public TicketCategory classify(String title, String description) {
        try {
            String response = chatClient.prompt()
                    .user(PROMPT_TEMPLATE.formatted(title, description))
                    .call()
                    .content();

            TicketCategory parsed = parseCategory(response);
            if (parsed != null) {
                return parsed;
            }
            log.warn("[AI-CLASSIFIER] Resposta não reconhecida ('{}'), aplicando fallback por regras.", response);
        } catch (Exception ex) {
            log.warn("[AI-CLASSIFIER] IA indisponível ({}), aplicando fallback por regras.", ex.getMessage());
        }
        return fallback.classify(title, description);
    }

    // ===== Apoio (extrai a categoria do texto livre da resposta) =====
    private TicketCategory parseCategory(String response) {
        if (response == null || response.isBlank()) {
            return null;
        }
        String normalized = response.trim().toUpperCase(Locale.ROOT);
        for (TicketCategory category : TicketCategory.values()) {
            if (normalized.contains(category.name())) {
                return category;
            }
        }
        return null;
    }
}
