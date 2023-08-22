package dev.backlog.domain.post.service;

import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponseDto;
import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.post.tmp.UserDetails;
import dev.backlog.domain.posthashtag.service.PostHashtagService;
import dev.backlog.domain.series.infrastructure.persistence.SeriesRepository;
import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostHashtagService postHashtagService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;

    @Transactional
    public PostResponseDto findPostById(UserDetails userDetails, Long postId) {
        return null;
    }

    @Transactional
    public Long create(PostCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Series series = seriesRepository.findByUserAndName(user, request.series())
                .orElse(null);
        Post post = request.toEntity(series, user);
        Post savedPost = postRepository.save(post);

        if (request.hashtags() != null) {
            postHashtagService.save(request.hashtags(), post);
        }
        return savedPost.getId();
    }

}
