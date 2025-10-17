package com.totals.service.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.totals.dto.BaLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private static final Pattern KENMERK_PATTERN = Pattern.compile("kenmerk[=:]\\s*(\\S+)");
    
    private final Path logFilePath;
    private final ObjectMapper objectMapper;
    
    public LogService(
            @Value("${logging.path:./logs}") String logPath,
            @Value("${logging.calculation.filename:ba-calc.log}") String calculationLogFile) {
        this.logFilePath = Paths.get(logPath, calculationLogFile);
        this.objectMapper = new ObjectMapper();
    }
    
    public List<BaLogEntry> getLogsForKenmerk(String kenmerk, int last) throws IOException {
        if (!Files.exists(logFilePath)) {
            logger.warn("Logbestand niet gevonden: Kenmerk={}", kenmerk);
            throw new IOException("Log bestand niet gevonden: " + logFilePath);
        }
        
        List<BaLogEntry> logEntries = new ArrayList<>();
        
        try (Stream<String> lines = Files.lines(logFilePath)) {
            List<String> filteredLines = lines
                .filter(line -> {
                    Matcher matcher = KENMERK_PATTERN.matcher(line);
                    return matcher.find() && kenmerk.equals(matcher.group(1));
                })
                .collect(Collectors.toList());
            
            // Take last N entries
            if (filteredLines.size() > last) {
                filteredLines = filteredLines.subList(filteredLines.size() - last, filteredLines.size());
            }
            
            // Parse JSON lines
            for (String line : filteredLines) {
                try {
                    JsonNode jsonNode = objectMapper.readTree(line);
                    String timestamp = jsonNode.has("@timestamp") ? jsonNode.get("@timestamp").asText() : "";
                    String level = jsonNode.has("level") ? jsonNode.get("level").asText() : "";
                    String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "";
                    String kenmerkValue = jsonNode.has("kenmerk") ? jsonNode.get("kenmerk").asText() : "";
                    logEntries.add(new BaLogEntry(timestamp, level, message, kenmerkValue));
                } catch (Exception e) {
                    // Skip corrupt log lines
                }
            }
        }
        
        return logEntries;
    }
}
