package chat.oriochat.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    private Long id;

    @NotBlank(message = "Sender name is required.")
    private String name;

    @NotNull(message = "Sender id is required.")
    private Long sender;

    @NotBlank(message = "Room number is required.")
    private String room;
    private MessageType type;

    @NotBlank(message = "Message content is required.")
    @Size(max = 256, message = "Message must not exceed 256 characters.")
    private String content;
}
