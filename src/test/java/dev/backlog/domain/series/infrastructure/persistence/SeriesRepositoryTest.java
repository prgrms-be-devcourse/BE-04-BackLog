package dev.backlog.domain.series.infrastructure.persistence;

import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static dev.backlog.domain.user.model.OAuthProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeriesRepositoryTest {

    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저와 시리즈의 이름으로 시리즈를 조회할 수 있다.")
    @Test
    void findByUserAndNameTest() {
        User user = User.builder()
                .oauthProvider(KAKAO)
                .oauthProviderId("test")
                .nickname("test")
                .email(new Email("test@example.com"))
                .profileImage("image")
                .blogTitle("blogTitle")
                .build();
        User savedUser = userRepository.save(user);

        Series series = Series.builder()
                .user(savedUser)
                .name("시리즈")
                .build();
        Series savedSeries = seriesRepository.save(series);

        Series foundSeries = seriesRepository.findByUserAndName(savedUser, "시리즈").get();
        assertThat(savedSeries).isEqualTo(foundSeries);
    }

}
