package dev.backlog.domain.post.api;


import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @ResponseStatus(HttpStatus.OK)
    public PostResponse findPost(@PathVariable Long postId, Long userId) {
        return postService.findPostById(postId, userId);
    }

}
