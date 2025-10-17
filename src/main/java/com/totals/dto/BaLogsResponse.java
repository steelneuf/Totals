package com.totals.dto;

import java.util.List;

public record BaLogsResponse(
    String kenmerk,
    List<BaLogEntry> logEntries
) {
    public static BaLogsResponse success(String kenmerk, List<BaLogEntry> logEntries) {
        return new BaLogsResponse(kenmerk, logEntries);
    }
}
