package org.example.liquoricecartservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.liquoricecartservice.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invalid arguments passed: " + ex.getMessage());
        body.put("error", ex.getClass().getSimpleName());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("error", "ResourceNotFound");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.warn("Access denied for user: '{}' with roles: {}", 
                auth.getName(), 
                auth.getAuthorities());
        
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Access denied");
        body.put("error", "Insufficient privileges");
        
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
