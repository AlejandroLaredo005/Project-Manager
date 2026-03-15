package com.alejandolaredo.projectmanager.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private ProjectMember assignee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Task() {
        // JPA
    }

    public Task(Project project, String title, User createdBy) {
        if (project == null) {
            throw new IllegalArgumentException("El proyecto no puede ser nulo");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede ser nulo o estar vacío");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("El creador no puede ser nulo");
        }
        this.project = project;
        this.title = title;
        this.createdBy = createdBy;
        this.priority = Priority.LOW;
        this.status = Status.TO_DO;
    }

    public Task(Project project, String title, String description, ProjectMember assignee,
                Priority priority, Status status, User createdBy) {
        if (project == null) {
            throw new IllegalArgumentException("El proyecto no puede ser nulo");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede ser nulo o estar vacío");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("El creador no puede ser nulo");
        }
        this.project = project;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.priority = priority != null ? priority : Priority.LOW;
        this.status = status != null ? status : Status.TO_DO;
        this.createdBy = createdBy;
    }

    public Long getId() {return id; }
    public void setId(Long id) {this.id = id;}

    public Project getProject() {return project; }
    public void setProject(Project project) {this.project = project;}

    public String getTitle() {return title; }
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description; }
    public void setDescription(String description) {this.description = description;}

    public ProjectMember getAssignee() {return assignee; }
    public void setAssignee(ProjectMember assignee) {this.assignee = assignee;}

    public Priority getPriority() {return priority; }
    public void setPriority(Priority priority) {this.priority = priority;}

    public Status getStatus() {return status; }
    public void setStatus(Status status) {this.status = status; }

    public User getCreatedBy() {return createdBy; }

    public Instant getCreatedAt() {return createdAt; }

    public Instant getUpdatedAt() {return updatedAt; }
}
