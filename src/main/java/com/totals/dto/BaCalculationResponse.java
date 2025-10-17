package com.totals.dto;

import java.util.List;

public record BaCalculationResponse(
    String message,
    String kenmerk,
    Integer bijdragePercentage,
    List<Double> totalen,
    long durationMs
) {
    public static BaCalculationResponse success(String kenmerk, Integer bijdragePercentage, 
                                            List<Double> totalen, long durationMs) {
        String message = String.format("BA totals calculated successfully for kenmerk: %s with bijdragePercentage: %d", 
                                     kenmerk, bijdragePercentage);
        return new BaCalculationResponse(message, kenmerk, bijdragePercentage, totalen, durationMs);
    }
}
