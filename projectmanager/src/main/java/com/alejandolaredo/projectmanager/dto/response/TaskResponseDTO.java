package com.alejandolaredo.projectmanager.dto.response;

import com.alejandolaredo.projectmanager.model.Priority;
import com.alejandolaredo.projectmanager.model.Status;
import com.alejandolaredo.projectmanager.model.Task;

import java.time.Instant;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;

    private Long assigneeId;
    private String assigneeName;

    private Long createdById;
    private String createdByName;

    private Instant createdAt;
    private Instant updatedAt;

    public TaskResponseDTO(Long id, String title, String description,
                   Priority priority, Status status, Long assigneeId,
                   String assigneeName, Long createdById, String createdByName,
                   Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskResponseDTO fromEntity(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getUser().getName() : null,
                task.getCreatedBy().getId(),
                task.getCreatedBy().getName(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public Priority getPriority() { return priority; }

    public Status getStatus() { return status; }

    public Long getAssigneeId() { return assigneeId; }

    public String getAssigneeName() { return assigneeName; }

    public Long getCreatedById() { return createdById; }

    public String getCreatedByName() { return createdByName; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
}
