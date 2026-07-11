package io.github.joaovictor.smartsupport.repository;

import io.github.joaovictor.smartsupport.entity.Client;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data de {@code Client}, com busca/checagem por e-mail. */
public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByEmail(String email);

    boolean existsByEmail(String email);
}
