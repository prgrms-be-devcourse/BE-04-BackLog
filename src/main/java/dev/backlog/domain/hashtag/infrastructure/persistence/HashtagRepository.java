package dev.backlog.domain.hashtag.infrastructure.persistence;

import dev.backlog.domain.hashtag.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String hashtag);

}
