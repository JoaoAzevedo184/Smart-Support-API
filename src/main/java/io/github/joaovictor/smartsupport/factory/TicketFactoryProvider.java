package io.github.joaovictor.smartsupport.factory;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Registro de fábricas indexado por {@link TicketCategory}. O Spring injeta todas
 * as {@link TicketFactory} e este provider resolve a certa em runtime, sem
 * {@code switch}/{@code if} — adicionar uma categoria é só criar a fábrica.
 */
@Component
public class TicketFactoryProvider {

    private final Map<TicketCategory, TicketFactory> factoriesByCategory;

    public TicketFactoryProvider(List<TicketFactory> factories) {
        this.factoriesByCategory = factories.stream()
                .collect(Collectors.toMap(TicketFactory::category, Function.identity()));
    }

    public TicketFactory getFactory(TicketCategory category) {
        TicketFactory factory = factoriesByCategory.get(category);
        if (factory == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria de chamado não suportada: " + category);
        }
        return factory;
    }
}
