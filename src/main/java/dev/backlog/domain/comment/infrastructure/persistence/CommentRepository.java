package dev.backlog.domain.comment.infrastructure.persistence;

import dev.backlog.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
