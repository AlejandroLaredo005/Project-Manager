package com.alejandolaredo.projectmanager.dto.response;

import com.alejandolaredo.projectmanager.model.User;

import java.time.Instant;

public class UserResponseDTO {

    private Long id;
    private String email;
    private String name;
    private Instant createdAt;

    public UserResponseDTO(Long id, String email, String name, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt()
        );
    }

    public Long getId() { return id; }

    public String getEmail() { return email; }

    public String getName() { return name; }

    public Instant getCreatedAt() { return createdAt; }
}
