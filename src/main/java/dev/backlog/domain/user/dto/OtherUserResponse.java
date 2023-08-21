package dev.backlog.domain.user.dto;

import dev.backlog.domain.user.model.User;

public record OtherUserResponse(
        String nickname,
        String introduction,
        String profileImage,
        String blogTitle
) {
    public OtherUserResponse(User user) {
        this(
                user.getNickname(),
                user.getIntroduction(),
                user.getProfileImage(),
                user.getBlogTitle()
        );
    }
}
