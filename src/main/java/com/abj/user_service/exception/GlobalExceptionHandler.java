package com.abj.user_service.exception;

import com.abj.user_service.dto.ErrorDetails;
import com.abj.user_service.dto.ResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("JvmTaintAnalysis")
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorDetails buildErrorDetails(HttpStatus status, String message, String errorCode) {
        String timestamp = Instant.now().toString();
        return new ErrorDetails(
                timestamp,
                status.value(),
                status.getReasonPhrase(),
                message,
                errorCode
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        var statusCode = ex.getStatusCode();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        String code = "RESPONSE_STATUS_" + statusCode.value();
        String traceId = UUID.randomUUID().toString();
        ErrorDetails details = buildErrorDetails(httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR, ex.getReason(), code);
        logger.warn("Handled ResponseStatusException: {} traceId={}", ex.getReason(), traceId);
        return new ResponseEntity<>(ResponseDTO.failure(details), statusCode);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String message = String.join("; ", errors);
        String traceId = UUID.randomUUID().toString();
        ErrorDetails details = buildErrorDetails(HttpStatus.BAD_REQUEST, message, "VALIDATION_ERROR");
        logger.warn("Validation failed: {} traceId={}", message, traceId);
        return new ResponseEntity<>(ResponseDTO.failure(details), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        ErrorDetails details = buildErrorDetails(HttpStatus.BAD_REQUEST, "Malformed JSON request", "MALFORMED_REQUEST");
        logger.warn("Malformed request: {} traceId={}", ex.getMessage(), traceId);
        return new ResponseEntity<>(ResponseDTO.failure(details), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .collect(Collectors.joining("; "));
        String traceId = UUID.randomUUID().toString();
        ErrorDetails details = buildErrorDetails(HttpStatus.BAD_REQUEST, message, "CONSTRAINT_VIOLATION");
        logger.warn("Constraint violations: {} traceId={}", message, traceId);
        return new ResponseEntity<>(ResponseDTO.failure(details), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        ErrorDetails details = buildErrorDetails(HttpStatus.NOT_FOUND, ex.getMessage(), "USER_NOT_FOUND");
        logger.warn("User not found: {} traceId={}", ex.getMessage(), traceId);
        return new ResponseEntity<>(ResponseDTO.failure(details), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Object> handleDuplicateEmail(DuplicateEmailException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        ErrorDetails details = buildErrorDetails(HttpStatus.CONFLICT, ex.getMessage(), "DUPLICATE_EMAIL");
        logger.warn("Duplicate email: {} traceId={}", ex.getMessage(), traceId);
        return new ResponseEntity<>(ResponseDTO.failure(details), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        String message = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        ErrorDetails details = buildErrorDetails(HttpStatus.CONFLICT, message.contains("email") ? "User already exists with this email" : "Data integrity violation", "DUPLICATE_EMAIL");
        logger.warn("Data integrity violation: {} traceId={}", message, traceId);
        return new ResponseEntity<>(ResponseDTO.failure(details), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        ErrorDetails details = buildErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "INTERNAL_ERROR");
        logger.error("Unhandled exception caught: traceId={}", traceId, ex);
        return new ResponseEntity<>(ResponseDTO.failure(details), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}