package dev.backlog.domain.post.infrastructure.persistence;

import dev.backlog.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
