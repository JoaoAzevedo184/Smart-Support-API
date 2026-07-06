package io.github.joaovictor.smartsupport.event.listener;

import io.github.joaovictor.smartsupport.adapter.NotificationSender;
import io.github.joaovictor.smartsupport.event.TicketAssignedEvent;
import io.github.joaovictor.smartsupport.event.TicketStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Atualiza o dashboard e repassa a notificação ao sistema legado
 * através do {@link NotificationSender} (Adapter).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardNotificationListener {

    private final NotificationSender notificationSender;

    @EventListener
    public void onStatusChanged(TicketStatusChangedEvent event) {
        log.info("[DASHBOARD] Atualizando painel: chamado {} -> {}",
                event.ticket().getId(), event.newStatus());

        notificationSender.notify(
                event.ticket().getId().toString(),
                "Chamado " + event.ticket().getId() + " mudou de " + event.previousStatus()
                        + " para " + event.newStatus()
        );
    }

    @EventListener
    public void onAssigned(TicketAssignedEvent event) {
        log.info("[DASHBOARD] Atualizando painel: chamado {} atribuído", event.ticket().getId());

        notificationSender.notify(
                event.ticket().getId().toString(),
                "Chamado " + event.ticket().getId() + " foi atribuído"
        );
    }
}
