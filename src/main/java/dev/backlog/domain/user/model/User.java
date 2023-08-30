package dev.backlog.domain.user.model;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider oauthProvider;

    @Column(nullable = false)
    private String oauthProviderId;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    @Embedded
    private Email email;

    @Column(nullable = false)
    private String profileImage;

    @Column(length = 100)
    private String introduction;

    @Column(nullable = false, length = 20)
    private String blogTitle;

    @Column(nullable = false)
    private LocalDate deletedDate;

    @Builder
    private User(
            OAuthProvider oauthProvider,
            String oauthProviderId,
            String nickname,
            Email email,
            String profileImage,
            String introduction,
            String blogTitle
    ) {
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.introduction = introduction;
        this.blogTitle = blogTitle;
        this.deletedDate = LocalDate.MAX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
