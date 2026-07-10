package io.github.joaovictor.smartsupport.repository;

import io.github.joaovictor.smartsupport.entity.SupportTeam;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data de {@code SupportTeam}, com busca/checagem por nome. */
public interface SupportTeamRepository extends JpaRepository<SupportTeam, UUID> {

    Optional<SupportTeam> findByName(String name);

    boolean existsByName(String name);
}
