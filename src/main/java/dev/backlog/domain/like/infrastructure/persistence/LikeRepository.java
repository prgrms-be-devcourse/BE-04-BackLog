package dev.backlog.domain.like.infrastructure.persistence;

import dev.backlog.domain.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
