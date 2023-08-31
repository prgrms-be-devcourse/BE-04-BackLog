package dev.backlog.domain.post.service;

import dev.backlog.common.config.JpaConfig;
import dev.backlog.common.config.TestContainerConfig;
import dev.backlog.domain.comment.infrastructure.persistence.CommentRepository;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.hashtag.infrastructure.persistence.HashtagRepository;
import dev.backlog.domain.like.infrastructure.persistence.LikeRepository;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.dto.PostSliceResponse;
import dev.backlog.domain.post.dto.PostSummaryResponse;
import dev.backlog.domain.post.dto.PostUpdateRequest;
import dev.backlog.domain.post.infrastructure.persistence.PostHashtagRepository;
import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.post.model.PostHashtag;
import dev.backlog.domain.series.infrastructure.persistence.SeriesRepository;
import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static dev.backlog.common.fixture.TestFixture.게시물1;
import static dev.backlog.common.fixture.TestFixture.게시물_모음;
import static dev.backlog.common.fixture.TestFixture.댓글_모음;
import static dev.backlog.common.fixture.TestFixture.시리즈1;
import static dev.backlog.common.fixture.TestFixture.유저1;
import static dev.backlog.common.fixture.TestFixture.좋아요1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(value = {JpaConfig.class})
@SpringBootTest
class PostServiceTest extends TestContainerConfig {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private PostHashtagRepository postHashtagRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    private User 유저1;
    private Post 게시물1;
    private List<Post> 게시물_모음;
    private List<Comment> 댓글_모음;

    @BeforeEach
    void setUp() {
        유저1 = 유저1();
        게시물1 = 게시물1(유저1, null);
        게시물_모음 = 게시물_모음(유저1, null);
        댓글_모음 = 댓글_모음(유저1, 게시물1);
    }

    @AfterEach
    void tearDown() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postHashtagRepository.deleteAll();
        hashtagRepository.deleteAll();
        postRepository.deleteAll();
        seriesRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("게시글을 상세 조회할 수 있다.")
    @Test
    void findPostById() {
        //given
        User user = userRepository.save(유저1);
        Post post = postRepository.save(게시물1);
        commentRepository.saveAll(댓글_모음);

        //when
        PostResponse postResponse = postService.findPostById(post.getId(), user.getId());

        //then
        assertThat(postResponse.postId()).isEqualTo(post.getId());
    }

    @DisplayName("같은 유저는 동일한 게시글의 조회수를 3시간동안 올릴 수 없다.")
    @Test
    void sameUserCannotIncreaseViewCountForSamePostWithin3Hours() {
        //given
        User user = userRepository.save(유저1);
        Post post = postRepository.save(게시물1);
        commentRepository.saveAll(댓글_모음);

        //when
        PostResponse firstSamePostResponse = postService.findPostById(post.getId(), user.getId());
        PostResponse secondSamePostResponse = postService.findPostById(post.getId(), user.getId());

        //then
        long increasedViewCount = 1L;

        assertAll(
                () -> assertThat(secondSamePostResponse.viewCount()).isEqualTo(increasedViewCount),
                () -> assertThat(firstSamePostResponse.viewCount()).isEqualTo(secondSamePostResponse.viewCount())
        );
    }

    @DisplayName("포스트 생성요청과 유저의 아이디를 받아 게시물을 저장할 수 있다.")
    @Test
    void createTest() {
        User user = userRepository.save(유저1);

        PostCreateRequest request = new PostCreateRequest(
                null,
                "제목",
                "내용",
                null,
                "요약",
                true,
                "url",
                "/path"
        );

        Long postId = postService.create(request, user.getId());
        assertThat(postId).isNotNull();
    }

    @DisplayName("사용자가 좋아요를 누른 글들을 최신 순으로 조회할 수 있다.")
    @Test
    void findLikedPostsByUser() {
        //given
        User user = userRepository.save(유저1);

        List<Post> posts = postRepository.saveAll(게시물_모음);
        for (Post post : posts) {
            Like like = 좋아요1(user, post);
            likeRepository.save(like);
        }

        PageRequest pageRequest = PageRequest.of(1, 20, Sort.Direction.DESC, "createdAt");

        //when
        PostSliceResponse postSliceResponse = postService.findLikedPostsByUser(user.getId(), pageRequest);

        //then
        assertAll(
                () -> assertThat(postSliceResponse.data()).isSortedAccordingTo(Comparator.comparing(PostSummaryResponse::createdAt).reversed()),
                () -> assertThat(postSliceResponse.hasNext()).isFalse(),
                () -> assertThat(postSliceResponse.numberOfElements()).isEqualTo(postSliceResponse.data().size())
        );
    }

    @DisplayName("사용자와 시리즈 이름으로 게시글들을 과거순으로 조회할 수 있다.")
    @Test
    void findPostsByUserAndSeries() {
        //given
        User user = userRepository.save(유저1);
        Series series = seriesRepository.save(시리즈1(user));
        postRepository.saveAll(게시물_모음(user, series));

        PageRequest pageRequest = PageRequest.of(1, 20, Sort.Direction.ASC, "createdAt");

        //when
        PostSliceResponse<PostSummaryResponse> postSliceResponse = postService.findPostsByUserAndSeries(user.getId(), series.getName(), pageRequest);

        //then
        assertAll(
                () -> assertThat(postSliceResponse.data()).isSortedAccordingTo(Comparator.comparing(PostSummaryResponse::createdAt)),
                () -> assertThat(postSliceResponse.hasNext()).isFalse(),
                () -> assertThat(postSliceResponse.numberOfElements()).isEqualTo(postSliceResponse.data().size())
        );
    }

    @DisplayName("최신 순서로 등록된 게시물 목록을 조회할 수 있다.")
    @Test
    void findPostsInLatestOrder() {
        //given
        userRepository.save(유저1);
        postRepository.saveAll(게시물_모음);

        PageRequest pageRequest = PageRequest.of(1, 20, Sort.Direction.DESC, "createdAt");

        //when
        PostSliceResponse<PostSummaryResponse> postSliceResponse = postService.findPostsInLatestOrder(pageRequest);

        //then
        assertAll(
                () -> assertThat(postSliceResponse.data()).isSortedAccordingTo(Comparator.comparing(PostSummaryResponse::createdAt).reversed()),
                () -> assertThat(postSliceResponse.hasNext()).isFalse(),
                () -> assertThat(postSliceResponse.numberOfElements()).isEqualTo(postSliceResponse.data().size())
        );
    }

    @DisplayName("게시물 업데이트의 대한 정보를 받아서 게시물을 업데이트한다.")
    @Test
    void updatePostTest() {
        userRepository.save(유저1);
        postRepository.save(게시물1);
        PostUpdateRequest request = getPostUpdateRequest();
        postService.updatePost(request, 게시물1.getId(), 유저1.getId());

        Post 변경된_게시물 = postRepository.findById(게시물1.getId()).get();
        List<PostHashtag> postHashtags = postHashtagRepository.findByPost(변경된_게시물);

        assertAll(
                () -> assertThat(변경된_게시물.getTitle()).isEqualTo("변경된 제목"),
                () -> assertThat(변경된_게시물.getContent()).isEqualTo("변경된 내용"),
                () -> assertThat(변경된_게시물.getSummary()).isEqualTo("변경된 요약"),
                () -> assertThat(변경된_게시물.getThumbnailImage()).isEqualTo("변경된 URL"),
                () -> assertThat(변경된_게시물.getIsPublic()).isFalse(),
                () -> assertThat(변경된_게시물.getPath()).isEqualTo("변경된 경로"),
                () -> assertThat(postHashtags.size()).isOne()
        );
    }

    private PostUpdateRequest getPostUpdateRequest() {
        return new PostUpdateRequest(
                null,
                "변경된 제목",
                "변경된 내용",
                Set.of("변경된 해쉬태그"),
                "변경된 요약",
                false,
                "변경된 URL",
                "변경된 경로"
        );
    }

}
