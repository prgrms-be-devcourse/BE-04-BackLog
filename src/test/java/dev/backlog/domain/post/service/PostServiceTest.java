package dev.backlog.domain.post.service;

import dev.backlog.common.config.JpaConfig;
import dev.backlog.common.config.TestContainerConfig;
import dev.backlog.common.util.TestStubUtil;
import dev.backlog.domain.comment.infrastructure.persistence.CommentRepository;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.like.infrastructure.persistence.LikeRepository;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.dto.PostSliceResponse;
import dev.backlog.domain.post.dto.PostSummaryResponse;
import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(value = {JpaConfig.class})
@ExtendWith(TestContainerConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostServiceTest {

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

    @BeforeEach
    void setUp() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("게시글을 상세 조회할 수 있다.")
    @Test
    void findPostById() {
        //given
        User user = TestStubUtil.createUser();
        userRepository.save(user);

        Post post = TestStubUtil.createPost(user, null);
        postRepository.save(post);

        Comment comment1 = TestStubUtil.createComment(user, post);
        Comment comment2 = TestStubUtil.createComment(user, post);
        commentRepository.saveAll(List.of(comment1, comment2));

        //when
        PostResponse postResponse = postService.findPostById(post.getId(), user.getId());

        //then
        assertThat(postResponse.postId()).isEqualTo(post.getId());
    }

    @DisplayName("같은 유저는 동일한 게시글의 조회수를 3시간동안 올릴 수 없다.")
    @Test
    void sameUserCannotIncreaseViewCountForSamePostWithin3Hours() {
        //given
        User user = TestStubUtil.createUser();
        User savedUser = userRepository.save(user);

        Post post = TestStubUtil.createPost(user, null);
        Post savedPost = postRepository.save(post);

        Comment comment1 = TestStubUtil.createComment(user, post);
        Comment comment2 = TestStubUtil.createComment(user, post);
        commentRepository.saveAll(List.of(comment1, comment2));

        //when
        PostResponse firstSamePostResponse = postService.findPostById(savedPost.getId(), savedUser.getId());
        PostResponse secondSamePostResponse = postService.findPostById(savedPost.getId(), savedUser.getId());

        //then
        long increasedViewCount = 1L;

        assertThat(secondSamePostResponse.viewCount()).isEqualTo(increasedViewCount);
        assertThat(firstSamePostResponse.viewCount()).isEqualTo(secondSamePostResponse.viewCount());
    }

    @DisplayName("포스트 생성요청과 유저의 아이디를 받아 게시물을 저장할 수 있다.")
    @Test
    void createTest() {
        User user = TestStubUtil.createUser();
        User savedUser = userRepository.save(user);

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

        Long postId = postService.create(request, savedUser.getId());
        assertThat(postId).isNotNull();
    }

    @DisplayName("사용자가 좋아요를 누른 글들을 최신 순으로 조회할 수 있다.")
    @Test
    void findLikedPostsByUser() {
        //given
        User user = TestStubUtil.createUser();
        userRepository.save(user);

        int postCount = 30;
        List<Post> posts = TestStubUtil.createPosts(user, null, postCount);
        postRepository.saveAll(posts);
        posts.stream()
                .forEach(post -> {
                    Like like = TestStubUtil.createLike(user, post);
                    likeRepository.save(like);
                });

        PageRequest pageRequest = PageRequest.of(1, 20, Sort.Direction.DESC, "createdAt");

        //when
        PostSliceResponse postSliceResponse = postService.findLikedPostsByUser(user.getId(), pageRequest);

        //then
        int expectedCount = 10;

        assertThat(postSliceResponse.data()).isSortedAccordingTo(Comparator.comparing(PostSummaryResponse::createdAt).reversed());
        assertThat(postSliceResponse.hasNext()).isFalse();
        assertThat(postSliceResponse.numberOfElements()).isEqualTo(expectedCount);
    }

}
