package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskRequest {

    @NotBlank
    private String title;

    private String description;

    public UpdateTaskRequest() {

    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
