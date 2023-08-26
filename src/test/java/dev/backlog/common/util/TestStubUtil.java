package dev.backlog.common.util;

import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.OAuthProvider;
import dev.backlog.domain.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestStubUtil {

    public static User createUser() {
        return buildUser();
    }

    public static Post createPost(User user, Series series) {
        return buildPost(user, series);
    }

    public static List<Post> createPosts(User user, Series series, int count) {
        List<Post> posts = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            Post post = buildPost(user, series);
            posts.add(post);
        }
        return posts;
    }

    public static Like createLike(User user, Post post) {
        return buildLike(user, post);
    }

    public static Comment createComment(User user, Post post) {
        return buildComment(user, post);
    }

    public static Series createSeries(User user) {
        return buildSeries(user);
    }

    private static Series buildSeries(User user) {
        return Series.builder()
                .user(user)
                .name("test")
                .build();
    }

    private static Like buildLike(User user, Post post) {
        return Like.builder()
                .post(post)
                .user(user)
                .build();
    }

    private static User buildUser() {
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

    private static Post buildPost(User user, Series series) {
        return Post.builder()
                .series(series)
                .user(user)
                .title("test")
                .content("test")
                .summary("test")
                .isPublic(true)
                .thumbnailImage("test")
                .path("test")
                .build();
    }

    private static Comment buildComment(User user, Post post) {
        return Comment.builder()
                .writer(user)
                .post(post)
                .content("test")
                .isDeleted(false)
                .build();
    }

}
