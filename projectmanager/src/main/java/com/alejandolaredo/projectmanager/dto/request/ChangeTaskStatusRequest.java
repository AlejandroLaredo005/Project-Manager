package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.Status;
import jakarta.validation.constraints.NotNull;

public class ChangeTaskStatusRequest {

    @NotNull
    private Long projectMemberId;

    @NotNull
    private Status status;

    public ChangeTaskStatusRequest() {

    }

    public Long getProjectMemberId() { return projectMemberId; }

    public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }
}
