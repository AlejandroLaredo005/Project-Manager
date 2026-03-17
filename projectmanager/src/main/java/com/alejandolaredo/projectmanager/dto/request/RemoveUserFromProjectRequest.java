package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.ProjectRole;
import jakarta.validation.constraints.NotNull;

public class RemoveUserFromProjectRequest {

    @NotNull
    private Long currentUserId;

    @NotNull
    private Long userIdToRemove;

    public RemoveUserFromProjectRequest() {

    }

    public Long getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(Long currentUserId) { this.currentUserId = currentUserId; }

    public Long getUserIdToRemove() { return userIdToRemove; }
    public void setUserIdToRemove(Long userIdToRemove) { this.userIdToRemove = userIdToRemove; }
}
