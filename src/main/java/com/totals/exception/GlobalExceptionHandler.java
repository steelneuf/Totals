package com.totals.exception;

import com.totals.dto.BaErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaErrorResponse> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        String path = extractPath(request);
        
        logger.error("ResponseStatusException: {} - {}", ex.getStatusCode(), ex.getReason());
        
        return ResponseEntity.status(ex.getStatusCode())
            .body(BaErrorResponse.of(ex.getStatusCode().value(), ex.getReason(), ex.getReason(), path));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String path = extractPath(request);
        
        logger.error("IllegalArgumentException: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaErrorResponse.of(400, "Bad Request", "Ongeldige parameter: " + ex.getMessage(), path));
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<BaErrorResponse> handleDataAccessException(DataAccessException ex, WebRequest request) {
        String path = extractPath(request);
        
        logger.error("DataAccessException: Database fout - {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(BaErrorResponse.of(503, "Service Unavailable", "Database niet beschikbaar na retry pogingen", path));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        String path = extractPath(request);
        
        logger.error("Onverwachte fout: error={}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(BaErrorResponse.of(500, "Internal Server Error", "Er is een onverwachte fout opgetreden", path));
    }
}