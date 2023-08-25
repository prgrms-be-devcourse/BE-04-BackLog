package dev.backlog.common.util;

import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.OAuthProvider;
import dev.backlog.domain.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestStubUtil {

    public static User createUser() {
        return getUser();
    }

    public static Post createPost(User user) {
        return getPost(user);
    }

    public static List<Post> createPosts(User user, int count) {
        List<Post> posts = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            Post post = getPost(user);
            posts.add(post);
        }
        return posts;
    }

    public static Like createLike(User user, Post post) {
        return getLike(user, post);
    }

    public static Comment createComment(User user, Post post) {
        return getComment(user, post);
    }

    private static Like getLike(User user, Post post) {
        return Like.builder()
                .post(post)
                .user(user)
                .build();
    }

    private static User getUser() {
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

    private static Post getPost(User user) {
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

    private static Comment getComment(User user, Post post) {
        return Comment.builder()
                .writer(user)
                .post(post)
                .content("test")
                .isDeleted(false)
                .build();
    }

}
