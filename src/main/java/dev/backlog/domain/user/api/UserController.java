package dev.backlog.domain.user.api;

import dev.backlog.domain.user.dto.UserDetailsResponse;
import dev.backlog.domain.user.dto.UserResponse;
import dev.backlog.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{nickname}")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponse findUserProfile(@PathVariable String nickname) {
        return userService.findUserProfile(nickname);
    }

    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDetailsResponse findMyProfile(@RequestHeader("Authorization") String token) {
        String realToken = token.substring(7);
        return userService.findMyProfile(realToken);
    }

}
