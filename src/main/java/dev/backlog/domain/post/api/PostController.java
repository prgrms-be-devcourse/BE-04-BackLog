package dev.backlog.domain.post.api;


import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.dto.PostSliceResponse;
import dev.backlog.domain.post.dto.PostSummaryResponse;
import dev.backlog.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody PostCreateRequest request, Long userId) {
        Long postId = postService.create(request, userId);

        return ResponseEntity.created(URI.create("/posts/" + postId)).build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> findPost(@PathVariable Long postId, Long userId) {
        PostResponse postResponse = postService.findPostById(postId, userId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/like")
    public ResponseEntity<PostSliceResponse<PostSummaryResponse>> findLikedPosts(Long userId,
                                                                                 @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PostSliceResponse<PostSummaryResponse> likedPosts = postService.findLikedPostsByUser(userId, pageable);
        return ResponseEntity.ok(likedPosts);
    }

    @GetMapping
    public ResponseEntity<PostSliceResponse<PostSummaryResponse>> findSeriesPosts(String series,
                                                                                  Long userId,
                                                                                  @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        PostSliceResponse<PostSummaryResponse> seriesPosts = postService.findPostsByUserAndSeries(userId, series, pageable);
        return ResponseEntity.ok(seriesPosts);
    }

}
