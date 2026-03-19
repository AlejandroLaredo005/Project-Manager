package com.alejandolaredo.projectmanager.exception;

import java.time.Instant;

public class ApiError {

    private String message;
    private int status;
    private Instant timestamp;

    public ApiError(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public Instant getTimestamp() { return timestamp; }
}