package dev.backlog.domain.user.service;

import dev.backlog.domain.auth.infrastructure.JwtTokenProvider;
import dev.backlog.domain.user.dto.OtherUserResponse;
import dev.backlog.domain.user.dto.UserResponse;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public OtherUserResponse findUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자는 찾을 수 현없습니다."));
        return new OtherUserResponse(user);
    }

    public UserResponse findMyProfile(String token) {
        Long userId = jwtTokenProvider.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자는 찾을 수 없습니다."));
        return new UserResponse(user);
    }
}
