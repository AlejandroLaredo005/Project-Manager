package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotNull;

public class UnassignTaskRequest {

    @NotNull
    private Long projectMemberId;

    public UnassignTaskRequest() {

    }

    public Long getProjectMemberId() { return projectMemberId; }
    public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }
}
