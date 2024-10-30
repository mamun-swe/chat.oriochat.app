package chat.oriochat.app.exception;

import chat.oriochat.app.dto.ChatMessageDTO;
import chat.oriochat.app.model.Room;
import chat.oriochat.app.service.RoomService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChatMessageValidator {
    private final Validator validator;
    private final RoomService roomService;

    // Constructor to inject Validator and RoomService
    public ChatMessageValidator(Validator validator, RoomService roomService) {
        this.validator = validator;
        this.roomService = roomService;
    }

    // Public method for validation
    public Map<String, List<String>> validateChatMessage(ChatMessageDTO messageDTO) {
        Set<ConstraintViolation<ChatMessageDTO>> violations = validator.validate(messageDTO);
        Map<String, List<String>> errorMap = new HashMap<>();

        if (!violations.isEmpty()) {
            for (ConstraintViolation<ChatMessageDTO> violation : violations) {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errorMap.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            }
        }

        return errorMap; // Return the error map, can be empty if no violations
    }

    // Method to validate room availability
    public Map<String, List<String>> validateRoom(String roomId) {
        Map<String, List<String>> errorMap = new HashMap<>();

        Optional<Room> availableRoom = roomService.getRoomByRoomId(roomId);
        if (availableRoom.isEmpty()) {
            errorMap.computeIfAbsent("room", k -> new ArrayList<>()).add("Room not available. Please enter a valid room number.");
        }

        return errorMap; // Return the error map, can be empty if the room is available
    }
}
