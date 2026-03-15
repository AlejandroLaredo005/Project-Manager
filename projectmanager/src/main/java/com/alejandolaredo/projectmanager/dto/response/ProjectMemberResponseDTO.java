package com.alejandolaredo.projectmanager.dto.response;

import com.alejandolaredo.projectmanager.model.ProjectMember;
import com.alejandolaredo.projectmanager.model.ProjectRole;

import java.time.Instant;

public class ProjectMemberResponseDTO {

    private Long id;

    private Long userId;
    private String userName;

    private ProjectRole role;
    private Instant joinedAt;

    public ProjectMemberResponseDTO(Long id, Long userId, String userName, ProjectRole role, Instant joinedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public static ProjectMemberResponseDTO fromEntity(ProjectMember projectMember) {
        return new ProjectMemberResponseDTO(
                projectMember.getId(),
                projectMember.getUser().getId(),
                projectMember.getUser().getName(),
                projectMember.getRole(),
                projectMember.getJoinedAt()
        );
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public String getUserName() { return userName; }

    public ProjectRole getRole() { return role; }

    public Instant getJoinedAt() { return joinedAt; }
}
