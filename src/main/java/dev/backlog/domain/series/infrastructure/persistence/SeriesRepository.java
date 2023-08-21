package dev.backlog.domain.series.infrastructure.persistence;

import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {

    Optional<Series> findByUserAndName(User user, String name);
}
