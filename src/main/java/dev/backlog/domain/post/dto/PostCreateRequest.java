package dev.backlog.domain.post.dto;

import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.model.User;

import java.util.Set;

public record PostCreateRequest(
        String series,
        String title,
        String content,
        Set<String> hashtags,
        String summary,
        boolean isPublic,
        String thumbnailImage,
        String path
) {

    public Post toEntity(Series series, User user) {
        return Post.builder()
                .series(series)
                .title(this.title)
                .user(user)
                .content(this.content)
                .summary(this.summary)
                .isPublic(this.isPublic)
                .thumbnailImage(this.thumbnailImage)
                .path(this.path)
                .build();
    }

}
