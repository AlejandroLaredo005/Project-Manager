package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateUsernameRequest {

    @NotBlank
    private String newUsername;

    public UpdateUsernameRequest() {

    }

    public String getNewUsername() { return newUsername; }

    public void setNewUsername(String newUsername) { this.newUsername = newUsername; }
}
