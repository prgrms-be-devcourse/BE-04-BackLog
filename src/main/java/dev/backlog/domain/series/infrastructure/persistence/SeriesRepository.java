package dev.backlog.domain.series.infrastructure.persistence;

import dev.backlog.domain.series.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Series, Long> {
}
