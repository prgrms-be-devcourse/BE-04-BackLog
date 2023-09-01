package dev.backlog.domain.like.infrastructure.persistence;

import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static dev.backlog.common.fixture.TestFixture.게시물1;
import static dev.backlog.common.fixture.TestFixture.유저1;
import static dev.backlog.common.fixture.TestFixture.좋아요1;

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
        User user1 = userRepository.save(유저1());
        User user2 = userRepository.save(유저1());
        Post post = postRepository.save(게시물1(user1, null));
        likeRepository.save(좋아요1(user1, post));
        likeRepository.save(좋아요1(user2, post));

        //when
        int likeCount = likeRepository.countByPost(post);

        //then
        int expectedCount = 2;
        Assertions.assertThat(likeCount).isEqualTo(expectedCount);
    }

}
