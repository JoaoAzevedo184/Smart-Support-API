package io.github.joaovictor.smartsupport.template;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import org.springframework.stereotype.Component;

/** Processador de suporte geral: usa o esqueleto padrão do template. */
@Component
public class SupportTicketProcessor extends CategoryProcessor {

    @Override
    public TicketCategory category() {
        return TicketCategory.SUPPORT;
    }
}
