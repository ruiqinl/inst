package com.rli.inst.rest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ErrorInfo {
    private Instant instant;
    private String message;
    private String details;

    public ErrorInfo(Instant timestamp, String message, String details) {
        this.instant = timestamp;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
