package dev.backlog.domain.reply.infrastructure.persistence;

import dev.backlog.domain.reply.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
