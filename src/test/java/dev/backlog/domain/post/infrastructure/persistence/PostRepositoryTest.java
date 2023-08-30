package dev.backlog.domain.post.infrastructure.persistence;

import dev.backlog.common.util.TestFixtureUtil;
import dev.backlog.domain.like.infrastructure.persistence.LikeRepository;
import dev.backlog.domain.like.model.Like;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.series.infrastructure.persistence.SeriesRepository;
import dev.backlog.domain.series.model.Series;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @DisplayName("공개된 게시글 중에서 특정 사용자가 남의 글에 좋아요를 누른 글들을 조회할 수 있다.")
    @Test
    void findLikedPostsByUserId() {
        //given
        User user = TestFixtureUtil.createUser();
        userRepository.save(user);

        int postCount = 30;
        List<Post> posts = TestFixtureUtil.createPosts(user, null, postCount);
        postRepository.saveAll(posts);
        posts.stream()
                .forEach(post -> {
                    Like like = TestFixtureUtil.createLike(user, post);
                    likeRepository.save(like);
                });

        PageRequest pageRequest = PageRequest.of(1, 20, Sort.Direction.DESC, "createdAt");

        //when
        Slice<Post> postSlice = postRepository.findLikedPostsByUserId(user.getId(), pageRequest);

        //then
        assertAll(
                () -> assertThat(postSlice.hasNext()).isFalse(),
                () -> assertThat(postSlice.getNumberOfElements()).isEqualTo(postSlice.getContent().size())
        );
    }

    @DisplayName("사용자와 시리즈 이름으로 게시글들을 조회할 수 있다.")
    @Test
    void findAllByUserAndSeries() {
        //given
        User user = TestFixtureUtil.createUser();
        userRepository.save(user);

        Series series = TestFixtureUtil.createSeries(user);
        seriesRepository.save(series);

        int postCount = 30;
        List<Post> posts = TestFixtureUtil.createPosts(user, series, postCount);
        postRepository.saveAll(posts);

        PageRequest pageRequest = PageRequest.of(1, 20, Sort.Direction.ASC, "createdAt");

        //when
        Slice<Post> postSlice = postRepository.findAllByUserAndSeries(user, series, pageRequest);

        //then
        assertAll(
                () -> assertThat(postSlice.hasNext()).isFalse(),
                () -> assertThat(postSlice.getNumberOfElements()).isEqualTo(postSlice.getContent().size())
        );
    }

}
