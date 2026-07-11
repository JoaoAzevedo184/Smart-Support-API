package io.github.joaovictor.smartsupport.adapter;

/**
 * Interface-alvo (Target) do padrão Adapter: a forma moderna e desacoplada de
 * enviar notificações que o restante da aplicação consome. As implementações
 * adaptam destinos concretos (sistema legado, webhook, log, no-op).
 */
public interface NotificationSender {

    void notify(String recipient, String message);
}
