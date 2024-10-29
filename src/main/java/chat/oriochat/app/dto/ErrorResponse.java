package chat.oriochat.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ErrorResponse<T> {
    private boolean status;
    private String message;
    private T errors;

    // Constructor
    public ErrorResponse(String message, T errors) {
        this.status = false;
        this.message = message;
        this.errors = errors;
    }
}
