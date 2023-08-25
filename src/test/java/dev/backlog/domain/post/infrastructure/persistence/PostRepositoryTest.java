package dev.backlog.domain.post.infrastructure.persistence;

import dev.backlog.common.util.TestStubUtil;
import dev.backlog.domain.like.infrastructure.persistence.LikeRepository;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @DisplayName("공개된 게시글 중에서 특정 사용자가 남의 글에 좋아요를 누른 글들을 최신 순으로 조회할 수 있다.")
    @Test
    void findLikedPostsByUserId() {
        //given
        User user = TestStubUtil.createUser();
        userRepository.save(user);

        int postCount = 30;
        List<Post> posts = TestStubUtil.createPosts(user, postCount);
        postRepository.saveAll(posts);
        posts.stream()
                .forEach(post -> {
                    Like like = TestStubUtil.createLike(user, post);
                    likeRepository.save(like);
                });

        PageRequest pageRequest = PageRequest.of(1, 20, Sort.Direction.DESC, "createdAt");

        //when
        Slice<Post> postSlice = postRepository.findLikedPostsByUserId(user.getId(), pageRequest);

        //then
        assertThat(postSlice.hasNext()).isFalse();
        assertThat(postSlice.getNumberOfElements()).isEqualTo(10);
    }

}
