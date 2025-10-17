package com.totals.dto;

import java.time.LocalDateTime;

public record BaErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
    public static BaErrorResponse of(int status, String error, String message, String path) {
        return new BaErrorResponse(LocalDateTime.now(), status, error, message, path);
    }
}
