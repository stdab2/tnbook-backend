package com.tnbook.tnbook.controller;

import com.tnbook.tnbook.dtos.CurrentUser;
import com.tnbook.tnbook.dtos.UserResponseDto;
import com.tnbook.tnbook.model.entity.User;
import com.tnbook.tnbook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@CurrentUser User user) {
        Optional<User> optionalCurrentUser = this.userService.findByEmail(user.getUsername());
        if (optionalCurrentUser.isPresent()) {
            UserResponseDto userResponseDto = new UserResponseDto(optionalCurrentUser.get().getId(), optionalCurrentUser.get().getUsername(), optionalCurrentUser.get().getAuthProvider());
            return ResponseEntity.ok(userResponseDto);
        }
        throw new RuntimeException();
    }

}
