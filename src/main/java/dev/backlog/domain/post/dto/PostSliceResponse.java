package dev.backlog.domain.post.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record PostSliceResponse<D>(int numberOfElements, boolean hasNext, List<D> data) {

    public static <T> PostSliceResponse<T> from(final Slice<T> posts) {
        return new PostSliceResponse<>(posts.getNumberOfElements(), posts.hasNext(), posts.getContent());
    }

}
