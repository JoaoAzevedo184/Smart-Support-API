package io.github.joaovictor.smartsupport.config;

import io.github.joaovictor.smartsupport.entity.Client;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.entity.Ticket;
import io.github.joaovictor.smartsupport.entity.User;
import io.github.joaovictor.smartsupport.entity.enums.TicketCategory;
import io.github.joaovictor.smartsupport.entity.enums.TicketPriority;
import io.github.joaovictor.smartsupport.entity.enums.TicketStatus;
import io.github.joaovictor.smartsupport.entity.enums.UserRole;
import io.github.joaovictor.smartsupport.repository.ClientRepository;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import io.github.joaovictor.smartsupport.repository.TicketRepository;
import io.github.joaovictor.smartsupport.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Popula o banco com dados de demonstração no profile "dev".
 * Não passa pela Facade/Chain para não gerar volume de notificações no boot.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final List<String> TEAM_NAMES = List.of("Bug Team", "Billing Team", "Support Team");
    private static final List<TicketCategory> CATEGORIES = List.of(TicketCategory.values());
    private static final List<TicketPriority> PRIORITIES = List.of(TicketPriority.values());
    private static final List<TicketStatus> STATUSES = List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED);

    // ===== Dependências =====
    private final ClientRepository clientRepository;
    private final SupportTeamRepository supportTeamRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    private final Faker faker = new Faker();

    // ===== Seed (idempotente: só popula banco vazio) =====
    @Override
    @Transactional
    public void run(String... args) {
        if (clientRepository.count() > 0) {
            log.info("[SEED] Banco já populado, seed de demonstração ignorado");
            return;
        }

        List<SupportTeam> teams = TEAM_NAMES.stream()
                .map(name -> supportTeamRepository.save(SupportTeam.builder()
                        .name(name)
                        .description(faker.company().catchPhrase())
                        .build()))
                .toList();

        List<User> agents = teams.stream()
                .map(team -> userRepository.save(User.builder()
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .password(faker.internet().password())
                        .role(UserRole.AGENT)
                        .supportTeam(team)
                        .build()))
                .toList();

        List<Client> clients = java.util.stream.IntStream.range(0, 5)
                .mapToObj(i -> clientRepository.save(Client.builder()
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .phone(faker.phoneNumber().phoneNumber())
                        .build()))
                .toList();

        for (int i = 0; i < 10; i++) {
            TicketCategory category = CATEGORIES.get(i % CATEGORIES.size());
            Client client = clients.get(i % clients.size());
            User agent = agents.get(i % agents.size());

            ticketRepository.save(Ticket.builder()
                    .title(faker.lorem().sentence(6))
                    .description(faker.lorem().paragraph(3))
                    .status(STATUSES.get(i % STATUSES.size()))
                    .priority(PRIORITIES.get(i % PRIORITIES.size()))
                    .category(category)
                    .client(client)
                    .assignedTeam(agent.getSupportTeam())
                    .assignedUser(agent)
                    .build());
        }

        log.info("[SEED] Dados de demonstração criados: {} times, {} agentes, {} clientes, 10 chamados",
                teams.size(), agents.size(), clients.size());
    }
}
