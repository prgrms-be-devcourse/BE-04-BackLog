package dev.backlog.domain.post.model;

import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(length = 100)
    private String summary;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column
    private String thumbnailImage;

    @Column(nullable = false)
    private String path;

    @Builder
    private Post(Series series,
                 User user,
                 String title,
                 String content,
                 String summary,
                 Boolean isPublic,
                 String thumbnailImage,
                 String path
    ) {
        this.series = series;
        this.user = user;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.isPublic = isPublic;
        this.thumbnailImage = thumbnailImage;
        this.path = path;
    }

    public String getWriterName() {
        return this.user.getNickname();
    }

}
