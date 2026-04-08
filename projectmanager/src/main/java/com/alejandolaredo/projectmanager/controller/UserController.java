package com.alejandolaredo.projectmanager.controller;

import com.alejandolaredo.projectmanager.dto.request.CreateUserRequest;
import com.alejandolaredo.projectmanager.dto.request.UpdateUsernameRequest;
import com.alejandolaredo.projectmanager.dto.response.UserResponseDTO;
import com.alejandolaredo.projectmanager.model.User;
import com.alejandolaredo.projectmanager.repository.UserRepository;
import com.alejandolaredo.projectmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
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

    @PatchMapping("/username")
    public UserResponseDTO updateUsername(Authentication authentication,
                                          @Valid @RequestBody UpdateUsernameRequest request) {
        User user = getUser(authentication);

        User updatedUser = userService.updateUsername(
                user.getId(),
                request.getNewUsername()
        );

        return UserResponseDTO.fromEntity(updatedUser);
    }

    @DeleteMapping
    public void deleteUser(Authentication authentication) {
        User user = getUser(authentication);

        userService.deleteUser(user.getId());
    }

    private User getUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
