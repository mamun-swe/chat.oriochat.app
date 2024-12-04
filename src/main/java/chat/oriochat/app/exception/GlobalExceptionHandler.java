package chat.oriochat.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        response.put("message", "Validation error.");

        // Create a map to store field errors
        Map<String, List<String>> errors = new HashMap<>();

        // Collect each field's error messages
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            // Add the error message as an array in the errors map
            errors.computeIfAbsent(fieldName, key -> new java.util.ArrayList<>()).add(errorMessage);
        });

        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);

        // Create a map to store field errors
        Map<String, List<String>> errors = new HashMap<>();

        // Add the error message as an array in the errors map
        errors.computeIfAbsent("server", key -> new java.util.ArrayList<>()).add("Something going wrong!");
//        response.put("errors", errors);

        System.out.print(ex.getMessage());
        response.put("errors", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
