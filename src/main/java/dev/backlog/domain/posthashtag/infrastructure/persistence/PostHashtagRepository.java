package dev.backlog.domain.posthashtag.infrastructure.persistence;

import dev.backlog.domain.posthashtag.domain.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
