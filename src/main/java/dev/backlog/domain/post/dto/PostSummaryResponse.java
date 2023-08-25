package dev.backlog.domain.post.dto;

import dev.backlog.domain.post.model.Post;

import java.time.LocalDateTime;

public record PostSummaryResponse(
        String thumbnailImage,
        String title,
        String summary,
        Long userId,
        LocalDateTime createdAt,
        int commentCount,
        int likeCount
) {

    public static PostSummaryResponse of(final Post post, final int commentCount, final int likeCount) {
        return new PostSummaryResponse(
                post.getThumbnailImage(),
                post.getTitle(),
                post.getSummary(),
                post.getUser().getId(),
                post.getCreatedAt(),
                commentCount,
                likeCount
        );
    }

}
