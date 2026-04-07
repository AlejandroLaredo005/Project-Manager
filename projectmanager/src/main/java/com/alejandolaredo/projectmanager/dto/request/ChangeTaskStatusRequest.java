package com.alejandolaredo.projectmanager.dto.request;

import com.alejandolaredo.projectmanager.model.Status;
import jakarta.validation.constraints.NotNull;

public class ChangeTaskStatusRequest {

    @NotNull
    private Status status;

    public ChangeTaskStatusRequest() {

    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
