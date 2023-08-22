package dev.backlog.domain.posthashtag.service;

import dev.backlog.domain.hashtag.infrastructure.persistence.HashtagRepository;
import dev.backlog.domain.hashtag.model.Hashtag;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.posthashtag.infrastructure.persistence.PostHashtagRepository;
import dev.backlog.domain.posthashtag.model.PostHashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostHashtagService {

    private final PostHashtagRepository postHashtagRepository;
    private final HashtagRepository hashtagRepository;

    public void save(Set<String> names, Post post) {
        List<Hashtag> hashtags = findHashtags(names);
        List<PostHashtag> postHashtags = hashtags.stream()
                .map(hashtag -> new PostHashtag(hashtag, post))
                .toList();

        postHashtagRepository.saveAll(postHashtags);
    }

    private List<Hashtag> findHashtags(Set<String> hashtags) {
        return hashtags.stream()
                .map(this::findOrCreate)
                .toList();
    }

    private Hashtag findOrCreate(String hashtag) {
        return hashtagRepository.findByName(hashtag)
                .orElseGet(() -> hashtagRepository.save(new Hashtag(hashtag)));
    }

}
