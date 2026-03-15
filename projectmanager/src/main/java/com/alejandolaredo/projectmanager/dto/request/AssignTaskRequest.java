package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignTaskRequest {

    @NotNull
    private Long assignerId;

    @NotNull
    private Long assignedId;

    public AssignTaskRequest() {

    }

    public Long getAssignerId() { return assignerId; }
    public void setAssignerId(Long assignerId) { this.assignerId = assignerId; }

    public Long getAssignedId() { return assignedId; }
    public void setAssignedId(Long assignedId) { this.assignedId = assignedId; }
}
