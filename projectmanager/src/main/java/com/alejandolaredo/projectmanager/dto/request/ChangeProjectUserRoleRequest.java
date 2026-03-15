package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.ProjectRole;
import jakarta.validation.constraints.NotNull;

public class ChangeProjectUserRoleRequest {

    @NotNull
    private Long adminUserId;

    @NotNull
    private Long userIdToChange;

    @NotNull
    private ProjectRole role;

    public ChangeProjectUserRoleRequest() {

    }

    public Long getAdminUserId() { return adminUserId; }
    public void setAdminUserId(Long adminUserId) { this.adminUserId = adminUserId; }

    public Long getUserIdToChange() { return userIdToChange; }
    public void setUserIdToChange(Long userIdToChange) { this.userIdToChange = userIdToChange; }

    public ProjectRole getRole() { return role; }
    public void setRole(ProjectRole role) { this.role = role; }
}
