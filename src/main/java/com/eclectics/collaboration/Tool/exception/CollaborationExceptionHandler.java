package com.eclectics.collaboration.Tool.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class CollaborationExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(Exception ex, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("time", new Date());
        response.put("status", status.value());
        response.put("error", ex.getClass().getSimpleName());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("time", new Date());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "ValidationFailed");
        response.put("messages", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CollaborationExceptions.ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(CollaborationExceptions.ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CollaborationExceptions.BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(CollaborationExceptions.BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CollaborationExceptions.UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(CollaborationExceptions.UnauthorizedException ex) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CollaborationExceptions.ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(CollaborationExceptions.ForbiddenException ex) {
        log.warn("Forbidden action: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CollaborationExceptions.FailedToReadMultiPartFile.class)
    public ResponseEntity<Object> handleFailedMultiPart(CollaborationExceptions.FailedToReadMultiPartFile ex) {
        log.warn("Failed to map Multipart file to byte: {}", ex.getMessage());
        return buildResponseEntity(ex, HttpStatus.MULTIPLE_CHOICES);
    }
}
