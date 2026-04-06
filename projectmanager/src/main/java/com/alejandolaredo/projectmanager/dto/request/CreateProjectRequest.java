package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProjectRequest {

    @NotBlank
    private String name;

    private String description;

    public CreateProjectRequest() {

    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
