package com.alejandolaredo.projectmanager.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uc_users_email", columnNames = {"email"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // almacenar hashed (BCrypt) en el futuro

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private boolean deleted = false;

    // Proyectos donde soy dueño
    @OneToMany(mappedBy = "owner")
    private Set<Project> ownedProjects = new HashSet<>();

    // Proyectos donde soy miembro
    @OneToMany(mappedBy = "user")
    private Set<ProjectMember> projectMemberships = new HashSet<>();

    // Métodos helper para proyectos propios
    public void addOwnedProject(Project project) {
        ownedProjects.add(project);
        project.setOwner(this);
    }

    public void removeOwnedProject(Project project) {
        ownedProjects.remove(project);
        project.setOwner(null);
    }

    // Métodos helper para membresías
    public void addProjectMembership(ProjectMember membership) {
        projectMemberships.add(membership);
        membership.setUser(this);
    }

    public void removeProjectMembership(ProjectMember membership) {
        projectMemberships.remove(membership);
        membership.setUser(null);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public User() {
        // JPA
    }

    // Constructor
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Set<Project> getOwnedProjects() { return ownedProjects; }

    public Set<ProjectMember> getProjectMemberships() { return projectMemberships; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}