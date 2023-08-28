package dev.backlog.domain.post.infrastructure.persistence;

import dev.backlog.domain.post.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT p 
            FROM Post p 
            INNER JOIN Like l ON l.post.id = p.id 
            WHERE p.isPublic = true AND l.user.id = :userId 
           """)
    Slice<Post> findLikedPostsByUserId(Long userId, Pageable pageable);

}
