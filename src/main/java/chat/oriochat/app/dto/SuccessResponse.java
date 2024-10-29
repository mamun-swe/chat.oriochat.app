package chat.oriochat.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse<T> {
    private boolean status;
    private String message;
    private T data;

    // Constructor
    public SuccessResponse(boolean status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
