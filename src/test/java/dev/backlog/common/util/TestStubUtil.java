package dev.backlog.common.util;

import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.OAuthProvider;
import dev.backlog.domain.user.model.User;

import java.util.UUID;

public class TestStubUtil {

    public static User createUser() {
        return User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthProviderId(String.valueOf(UUID.randomUUID()))
                .nickname("test")
                .email(new Email("test"))
                .profileImage("test")
                .introduction("test")
                .blogTitle("test")
                .build();
    }

    public static Post createPost(User user) {
        return Post.builder()
                .series(null)
                .user(user)
                .title("test")
                .content("test")
                .summary("test")
                .isPublic(true)
                .thumbnailImage("test")
                .path("test")
                .build();
    }

    public static Comment createComment(User user, Post post) {
        return Comment.builder()
                .writer(user)
                .post(post)
                .content("test")
                .isDeleted(false)
                .build();
    }

    public static Like createLike(User user, Post post) {
        return Like.builder()
                .post(post)
                .user(user)
                .build();
    }

}
