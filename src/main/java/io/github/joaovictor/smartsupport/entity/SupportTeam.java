package io.github.joaovictor.smartsupport.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Equipe de suporte que recebe chamados atribuídos e agrega usuários. */
@Entity
@Table(name = "support_teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "users")
public class SupportTeam {

    // ===== Identidade =====
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ===== Atributos =====
    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    // ===== Relacionamentos =====
    @Builder.Default
    @OneToMany(mappedBy = "supportTeam", cascade = CascadeType.PERSIST)
    private List<User> users = new ArrayList<>();

    // ===== Auditoria =====
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Callbacks JPA =====
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
