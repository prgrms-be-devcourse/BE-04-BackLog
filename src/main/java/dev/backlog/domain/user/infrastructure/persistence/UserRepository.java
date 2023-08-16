package dev.backlog.domain.user.infrastructure.persistence;

import dev.backlog.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
