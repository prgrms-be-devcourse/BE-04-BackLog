package dev.backlog.domain.like.infrastructure.persistence;

import dev.backlog.common.util.TestStubUtil;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("특정 게시물에 달린 좋아요 수를 조회할 수 있다.")
    @Test
    void countByPost() {
        //given
        User user1 = TestStubUtil.createUser();
        userRepository.save(user1);

        User user2 = TestStubUtil.createUser();
        userRepository.save(user2);

        Post post = TestStubUtil.createPost(user1);
        postRepository.save(post);

        Like like1 = TestStubUtil.createLike(user1, post);
        likeRepository.save(like1);

        Like like2 = TestStubUtil.createLike(user2, post);
        likeRepository.save(like2);

        //when
        int likeCount = likeRepository.countByPost(post);

        //then
        int expectedCount = 2;
        Assertions.assertThat(likeCount).isEqualTo(expectedCount);
    }

}
