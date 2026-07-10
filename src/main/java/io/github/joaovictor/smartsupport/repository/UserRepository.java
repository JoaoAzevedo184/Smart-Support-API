package io.github.joaovictor.smartsupport.repository;

import io.github.joaovictor.smartsupport.entity.User;
import io.github.joaovictor.smartsupport.entity.enums.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data de {@code User}, com buscas por e-mail, equipe e papel. */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findBySupportTeamId(UUID supportTeamId);

    List<User> findByRole(UserRole role);
}
