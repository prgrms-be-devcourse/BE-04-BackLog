package dev.backlog.domain.post.infrastructure.persistence;

import dev.backlog.domain.hashtag.infrastructure.persistence.HashtagRepository;
import dev.backlog.domain.hashtag.model.Hashtag;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.post.model.PostHashtag;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static dev.backlog.domain.auth.model.oauth.OAuthProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostHashtagRepositoryTest {

    @Autowired
    private PostHashtagRepository postHashtagRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private HashtagRepository hashtagRepository;

    private User 유저1;
    private Post 게시물1;
    private List<Hashtag> 해쉬태그;

    @BeforeEach
    void setUp() {
        유저1 = User.builder()
                .oauthProvider(KAKAO)
                .oauthProviderId("KAKAO_ID")
                .nickname("닉네임")
                .email(new Email("이메일"))
                .profileImage("프로필사진URL")
                .introduction("소개글")
                .blogTitle("블로그 이름")
                .build();

        게시물1 = Post.builder()
                .series(null)
                .user(유저1)
                .title("제목")
                .content("내용")
                .summary("요약")
                .isPublic(true)
                .thumbnailImage("썸네일URL")
                .path("경로")
                .build();

        해쉬태그 = createHashtags("해쉬태그", "해쉬태그1", "해쉬태그2", "해쉬태그3");
    }

    @DisplayName("PostHashtag에서 Post를 찾아 PostHashtag를 삭제한다.")
    @Test
    void deleteAllByPostTest() {
        userRepository.save(유저1);
        postRepository.save(게시물1);
        hashtagRepository.saveAll(해쉬태그);
        List<PostHashtag> postHashtags = createPostHashtags(게시물1, 해쉬태그);
        postHashtagRepository.saveAll(postHashtags);

        postHashtagRepository.deleteAllByPost(게시물1);

        List<PostHashtag> findPostHashtag = postHashtagRepository.findByPost(게시물1);
        assertThat(findPostHashtag).isEmpty();
    }
    
    @DisplayName("게시물로 게시물에 등록된 해쉬태그를 검색할 수 있다.")
    @Test
    void findByPostTest(){
        userRepository.save(유저1);
        postRepository.save(게시물1);
        hashtagRepository.saveAll(해쉬태그);
        List<PostHashtag> postHashtags = createPostHashtags(게시물1, 해쉬태그);
        postHashtagRepository.saveAll(postHashtags);

        List<PostHashtag> 포스트_해쉬태그 = postHashtagRepository.findByPost(게시물1);

        포스트_해쉬태그.forEach(a -> System.out.println(a.getHashtag()));
        assertThat(포스트_해쉬태그).hasSize(해쉬태그.size());
     }

    @DisplayName("해쉬태그를 통해 사용되고 있는 게시물이 없다면 True를 반환한다.")
    @Test
    void existsByHashtagTest() {
        userRepository.save(유저1);
        postRepository.save(게시물1);
        hashtagRepository.saveAll(해쉬태그);
        List<PostHashtag> postHashtags = createPostHashtags(게시물1, 해쉬태그);
        postHashtagRepository.saveAll(postHashtags);

        Hashtag hashtag = 해쉬태그.get(0);
        boolean 해쉬태그_유무 = postHashtagRepository.existsByHashtag(hashtag);
        assertThat(해쉬태그_유무).isTrue();
    }

    private List<Hashtag> createHashtags(String... 해쉬태그) {
        return Arrays.stream(해쉬태그)
                .map(Hashtag::new)
                .toList();
    }

    private List<PostHashtag> createPostHashtags(Post post, List<Hashtag> hashtags) {
        return hashtags.stream()
                .map(tag -> new PostHashtag(tag, post))
                .toList();
    }

}
