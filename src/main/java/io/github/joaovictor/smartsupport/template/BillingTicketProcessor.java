package io.github.joaovictor.smartsupport.template;

import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import org.springframework.stereotype.Component;

/** Processador de cobrança: usa o esqueleto padrão do template, sem passos extras. */
@Component
public class BillingTicketProcessor extends CategoryProcessor {

    @Override
    public TicketCategory category() {
        return TicketCategory.BILLING;
    }
}
