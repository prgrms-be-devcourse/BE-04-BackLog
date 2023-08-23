package dev.backlog.domain.post.service;

import dev.backlog.domain.comment.infrastructure.persistence.CommentRepository;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private static final String REDIS_KEY_PREFIX = "user:%s:post:%s";

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public PostResponse findPostById(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        List<Comment> comments = commentRepository.findAllByPost(post);
        String userAndPostRedisKey = String.format(REDIS_KEY_PREFIX, userId, postId);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(userAndPostRedisKey))) {
            Long currentViewCount = post.getViewCount();
            Long increasedViewCount = currentViewCount + 1;
            redisTemplate.opsForValue().set(userAndPostRedisKey, String.valueOf(increasedViewCount));
            redisTemplate.expire(userAndPostRedisKey, Duration.ofHours(3));
            post.updateViewCount(increasedViewCount);
        }
        return PostResponse.from(post, comments);
    }

}
