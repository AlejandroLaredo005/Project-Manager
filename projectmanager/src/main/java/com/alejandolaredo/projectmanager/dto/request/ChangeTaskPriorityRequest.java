package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.Priority;
import jakarta.validation.constraints.NotNull;

public class ChangeTaskPriorityRequest {

    @NotNull
    private Long projectMemberId;

    @NotNull
    private Priority priority;

    public ChangeTaskPriorityRequest() {

    }

    public Long getProjectMemberId() { return projectMemberId; }
    public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
