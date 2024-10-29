package chat.oriochat.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDTO {
    private String roomId;

    @NotBlank(message = "Room name is required.")
    @Size(min = 5, max = 50, message = "Room name must be between 5 and 50 characters.")
    private String name;
}
