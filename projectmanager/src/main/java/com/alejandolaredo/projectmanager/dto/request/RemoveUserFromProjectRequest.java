package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.ProjectRole;
import jakarta.validation.constraints.NotNull;

public class RemoveUserFromProjectRequest {

    @NotNull
    private Long userIdToRemove;

    public RemoveUserFromProjectRequest() {

    }

    public Long getUserIdToRemove() { return userIdToRemove; }
    public void setUserIdToRemove(Long userIdToRemove) { this.userIdToRemove = userIdToRemove; }
}
