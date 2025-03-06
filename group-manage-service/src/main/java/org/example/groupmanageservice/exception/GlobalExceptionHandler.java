package org.example.groupmanageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gs.fw.common.mithra.MithraBusinessException;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException ex) {
        return new ResponseEntity<>(Map.of("error", "Forbidden", "message", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MithraBusinessException.class)
    public ResponseEntity<Map<String, String>> handleMithraBusinessException(MithraBusinessException ex) {
        return new ResponseEntity<>(Map.of("error", "Mithra Business Exception", "message", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(Map.of("error", "Not Found", "message", ex.getMessage()), HttpStatus.NOT_FOUND);
    }




    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException ex) {
        return new ResponseEntity<>(Map.of("error", "Not Found", "message", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(Map.of("error", "Bad Request", "message", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return new ResponseEntity<>(Map.of("error", "Conflict", "message", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        // In production, you may want to log the error without exposing internal details.
        return new ResponseEntity<>(Map.of("error", "Internal Server Error", "message", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

