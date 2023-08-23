package dev.backlog.domain.post.infrastructure.persistence;

import dev.backlog.domain.post.model.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
