package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignTaskRequest {

    @NotNull
    private Long assignedId;

    public AssignTaskRequest() {

    }

    public Long getAssignedId() { return assignedId; }
    public void setAssignedId(Long assignedId) { this.assignedId = assignedId; }
}
