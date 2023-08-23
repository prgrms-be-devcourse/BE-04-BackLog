package dev.backlog.domain.post.dto;

import dev.backlog.domain.comment.dto.CommentResponse;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.series.dto.SeriesResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record PostResponse(
        Long postId,
        SeriesResponse series,
        Long userId,
        String title,
        Long viewCount,
        String content,
        String summary,
        Boolean isPublic,
        String path,
        LocalDateTime createdAt,
        List<CommentResponse> comments
) {

    public static PostResponse from(Post post, List<Comment> comments) {

        if (Objects.isNull(post.getSeries())) {
            return new PostResponse(
                    post.getId(),
                    null,
                    post.getUser().getId(),
                    post.getTitle(),
                    post.getViewCount(),
                    post.getContent(),
                    post.getSummary(),
                    post.getIsPublic(),
                    post.getPath(),
                    post.getCreatedAt(),
                    comments.stream()
                            .map(CommentResponse::from)
                            .toList()
            );
        }

        return new PostResponse(
                post.getId(),
                SeriesResponse.from(post.getSeries()),
                post.getUser().getId(),
                post.getTitle(),
                post.getViewCount(),
                post.getContent(),
                post.getSummary(),
                post.getIsPublic(),
                post.getPath(),
                post.getCreatedAt(),
                comments.stream()
                        .map(CommentResponse::from)
                        .toList()
        );
    }

}
