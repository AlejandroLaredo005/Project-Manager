package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.ProjectRole;
import jakarta.validation.constraints.NotNull;

public class AddUserToProjectRequest {

    @NotNull
    private Long currentUserId;

    @NotNull
    private Long userIdToAdd;

    @NotNull
    private ProjectRole role;

    public AddUserToProjectRequest() {

    }

    public Long getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(Long currentUserId) { this.currentUserId = currentUserId; }

    public Long getUserIdToAdd() { return userIdToAdd; }
    public void setUserIdToAdd(Long userIdToAdd) { this.userIdToAdd = userIdToAdd; }

    public ProjectRole getRole() { return role; }
    public void setRole(ProjectRole role) { this.role = role; }
}
