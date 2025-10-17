package com.totals.dto;

public record BaLogEntry(
    String timestamp,
    String level,
    String message,
    String kenmerk
) {
}
