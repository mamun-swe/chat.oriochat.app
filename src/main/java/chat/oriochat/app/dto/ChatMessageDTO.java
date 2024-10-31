package chat.oriochat.app.dto;

import chat.oriochat.app.model.ChatMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ChatMessageDTO {
    private Long id;

    @NotNull(message = "Sender id is required.")
    private Long senderId;

    @NotBlank(message = "Sender name is required.")
    private String senderName;

    @NotBlank(message = "Room number is required.")
    private String room;
    private ChatMessage.MessageType type;

    @NotBlank(message = "Message content is required.")
    @Size(max = 256, message = "Message must not exceed 256 characters.")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Custom date-time format
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Return createdAt as a formatted String
    public String getCreatedAt() {
        return createdAt != null ? createdAt.format(FORMATTER) : null;
    }

    // Return updatedAt as a formatted String
    public String getUpdatedAt() {
        return updatedAt != null ? updatedAt.format(FORMATTER) : null;
    }
}
