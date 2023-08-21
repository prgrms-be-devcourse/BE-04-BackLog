package dev.backlog.domain.user.api;

import dev.backlog.domain.user.dto.OtherUserResponse;
import dev.backlog.domain.user.dto.UserResponse;
import dev.backlog.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public OtherUserResponse findUserProfile(@PathVariable Long id) {
        return userService.findUserProfile(id);
    }

    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public UserResponse findMyProfile(@RequestHeader("Authorization") String token) {
        String realToken = token.substring(7);
        return userService.findMyProfile(realToken);
    }
}
