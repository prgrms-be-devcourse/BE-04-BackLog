package dev.backlog.domain.post.dto;

import java.util.Set;

public record PostUpdateRequest(
        String series,
        String title,
        String content,
        Set<String> hashtags,
        String summary,
        boolean isPublic,
        String thumbnailImage,
        String path
) {
}
