package dev.backlog.domain.user.dto;

import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.User;

public record UserResponse(
        String nickname,
        String introduction,
        String profileImage,
        String blogTitle,
        Email email) {
    public UserResponse(User user) {
        this(
                user.getNickname(),
                user.getIntroduction(),
                user.getProfileImage(),
                user.getBlogTitle(),
                user.getEmail()
        );
    }
}
