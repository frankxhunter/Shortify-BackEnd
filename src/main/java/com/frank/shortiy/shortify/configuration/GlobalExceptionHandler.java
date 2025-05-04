package com.frank.shortiy.shortify.configuration;

import com.frank.shortiy.shortify.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();

        // Obtener errores de validacion
        ex.getBindingResult().getAllErrors().forEach((e) -> {
            String fieldname = ((FieldError) e).getField();
            String message = e.getDefaultMessage();

            errorMessage.append("Field: ").append(fieldname)
                    .append(" - ").append(message).append(";\n");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException ex) {
        StringBuilder errorMessage = new StringBuilder();

        // Obtener errores de validacion
        ex.getConstraintViolations().forEach((e) -> {
            String fieldName = e.getPropertyPath().toString();
            if (fieldName.contains(".")) {
                fieldName.substring(fieldName.indexOf(".") + 1);
            }
            String message = e.getMessage();

            errorMessage.append("Field: ").append(fieldName)
                    .append(" - ").append(message).append(";\n");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(ResourceNotFoundException ex,
                                                          HttpServletRequest req) {
        log.warn("Resource not found: {} - Path {}", ex.getMessage(), req.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMatchException(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("Invalid type match with %s parameter", ex.getName());
        log.warn(msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleGeneralException(DataIntegrityViolationException e) {
        e.printStackTrace();
        if (e.getRootCause() != null && e.getRootCause().getMessage().contains("Duplicate entry") &&
                e.getRootCause().getMessage().contains("EMAIL")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Email: An account with this email already exists");

        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error ocurred: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error ocurred: " + e.getMessage());
    }
}
