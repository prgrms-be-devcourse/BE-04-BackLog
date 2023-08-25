package dev.backlog.domain.post.service;

import dev.backlog.common.config.TestContainerConfig;
import dev.backlog.common.util.TestStubUtil;
import dev.backlog.domain.comment.infrastructure.persistence.CommentRepository;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @BeforeEach
    void setUp() {
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

        Post post = TestStubUtil.createPost(user);
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

        Post post = TestStubUtil.createPost(user);
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

}
