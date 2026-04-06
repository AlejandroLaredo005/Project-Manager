package com.alejandolaredo.projectmanager.dto.request;

import jakarta.validation.constraints.NotNull;

public class TransferProjectOwnership {

    @NotNull
    private Long userIdToChange;

    public TransferProjectOwnership() {

    }

    public Long getUserIdToChange() { return userIdToChange; }
    public void setUserIdToChange(Long userIdToChange) { this.userIdToChange = userIdToChange; }
}
