package com.alejandolaredo.projectmanager.controller;

import com.alejandolaredo.projectmanager.dto.request.CreateUserRequest;
import com.alejandolaredo.projectmanager.dto.request.UpdateUsernameRequest;
import com.alejandolaredo.projectmanager.dto.response.UserResponseDTO;
import com.alejandolaredo.projectmanager.model.User;
import com.alejandolaredo.projectmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDTO createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );

        return UserResponseDTO.fromEntity(user);
    }

    @PatchMapping("/{userId}/username")
    public UserResponseDTO updateUsername(@PathVariable Long userId,
                                          @Valid @RequestBody UpdateUsernameRequest request) {
        User user = userService.updateUsername(
                userId,
                request.getNewUsername()
        );

        return UserResponseDTO.fromEntity(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
