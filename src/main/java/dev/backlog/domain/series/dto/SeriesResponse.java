package dev.backlog.domain.series.dto;

import dev.backlog.domain.series.model.Series;

public record SeriesResponse(
        Long seriesId,
        String seriesName
) {

    public static SeriesResponse from(Series series) {
        return new SeriesResponse(
                series.getId(),
                series.getName()
        );
    }

}
