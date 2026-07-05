package io.github.joaovictor.smartsupport.template;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CategoryProcessorProvider {

    private final Map<TicketCategory, CategoryProcessor> processorsByCategory;

    public CategoryProcessorProvider(List<CategoryProcessor> processors) {
        this.processorsByCategory = processors.stream()
                .collect(Collectors.toMap(CategoryProcessor::category, Function.identity()));
    }

    public CategoryProcessor getProcessor(TicketCategory category) {
        CategoryProcessor processor = processorsByCategory.get(category);
        if (processor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria de chamado não suportada: " + category);
        }
        return processor;
    }
}
