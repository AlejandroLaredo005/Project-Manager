package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.Priority;
import jakarta.validation.constraints.NotNull;

public class ChangeTaskPriorityRequest {

    @NotNull
    private Priority priority;

    public ChangeTaskPriorityRequest() {

    }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
