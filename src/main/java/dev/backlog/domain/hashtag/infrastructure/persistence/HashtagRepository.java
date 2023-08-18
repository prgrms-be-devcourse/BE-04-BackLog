package dev.backlog.domain.hashtag.infrastructure.persistence;

import dev.backlog.domain.hashtag.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}
