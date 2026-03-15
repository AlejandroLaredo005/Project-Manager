package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotNull;

public class LeaveProjectRequest {

    @NotNull
    private Long userId;

    public LeaveProjectRequest() {

    }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }
}
