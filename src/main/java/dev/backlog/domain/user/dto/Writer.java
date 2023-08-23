package dev.backlog.domain.user.dto;

import dev.backlog.domain.user.model.User;

public record Writer(
        Long userId,
        String nickname
) {

    public static Writer from(User user) {
        return new Writer(user.getId(), user.getNickname());
    }

}
