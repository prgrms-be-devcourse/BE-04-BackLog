package dev.backlog.common.fixture;

import dev.backlog.domain.hashtag.model.Hashtag;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.backlog.domain.auth.model.oauth.OAuthProvider.KAKAO;

public class TestFixture {

    public static User 유저1() {
        return User.builder()
                .oauthProvider(KAKAO)
                .oauthProviderId("KAKAO_ID")
                .nickname("닉네임")
                .email(new Email("이메일"))
                .profileImage("프로필사진URL")
                .introduction("소개글")
                .blogTitle("블로그 이름")
                .build();
    }

    public static Post 게시물1(User user) {
        return Post.builder()
                .series(null)
                .user(user)
                .title("제목")
                .content("내용")
                .summary("요약")
                .isPublic(true)
                .thumbnailImage("썸네일URL")
                .path("경로")
                .build();
    }

    public static List<Post> 게시물_모음(User user) {
        List<Post> posts = new ArrayList<>();
        for (int index = 0; index < 30; index++) {
            Post post = 게시물1(user);
            posts.add(post);
        }
        return posts;
    }

    public static List<Hashtag> 해쉬태그_모음() {
        return createHashtags("해쉬태그", "해쉬태그1", "해쉬태그2", "해쉬태그3");
    }

    private static List<Hashtag> createHashtags(String... 해쉬태그) {
        return Arrays.stream(해쉬태그)
                .map(Hashtag::new)
                .toList();
    }

}
