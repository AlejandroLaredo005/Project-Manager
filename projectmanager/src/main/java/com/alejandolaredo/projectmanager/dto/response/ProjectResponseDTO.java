package com.alejandolaredo.projectmanager.dto.response;

import com.alejandolaredo.projectmanager.model.Project;

import java.time.Instant;

public class ProjectResponseDTO {

    private Long id;
    private String name;
    private String description;

    private Long ownerId;
    private String ownerName;

    private Instant createdAt;

    private int memberCount;
    private int taskCount;

    public ProjectResponseDTO(Long id, String name, String description,
                      Long ownerId, String ownerName, Instant createdAt,
                      int memberCount, int taskCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.memberCount = memberCount;
        this.taskCount = taskCount;
    }

    public static ProjectResponseDTO fromEntity(Project project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getName(),
                project.getCreatedAt(),
                project.getMembers().size(),
                project.getTasks().size()
        );
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public Long getOwnerId() { return ownerId; }

    public String getOwnerName() { return ownerName; }

    public Instant getCreatedAt() { return createdAt; }

    public int getMemberCount() { return memberCount; }

    public int getTaskCount() { return taskCount; }
}
