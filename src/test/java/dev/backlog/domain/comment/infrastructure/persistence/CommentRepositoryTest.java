package dev.backlog.domain.comment.infrastructure.persistence;

import dev.backlog.common.util.TestFixtureUtil;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("특정 게시글에 달린 댓글 목록을 조회할 수 있다.")
    @Test
    void findAllByPost() {
        //given
        User user = TestFixtureUtil.createUser();
        userRepository.save(user);

        Post post = TestFixtureUtil.createPost(user, null);
        postRepository.save(post);

        Comment comment1 = TestFixtureUtil.createComment(user, post);
        Comment comment2 = TestFixtureUtil.createComment(user, post);
        commentRepository.saveAll(List.of(comment1, comment2));

        //when
        List<Comment> comments = commentRepository.findAllByPost(post);

        //then
        int expectedSize = 2;
        assertThat(comments).hasSize(expectedSize);
    }

    @DisplayName("특정 게시글에 달린 댓글 수를 조회할 수 있다.")
    @Test
    void countByPost() {
        //given
        User user = TestFixtureUtil.createUser();
        userRepository.save(user);

        Post post = TestFixtureUtil.createPost(user, null);
        postRepository.save(post);

        Comment comment1 = TestFixtureUtil.createComment(user, post);
        Comment comment2 = TestFixtureUtil.createComment(user, post);
        commentRepository.saveAll(List.of(comment1, comment2));

        //when
        int commentCount = commentRepository.countByPost(post);

        //then
        int expectedCount = 2;
        assertThat(commentCount).isEqualTo(expectedCount);
    }

}
