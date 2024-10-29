package chat.oriochat.app.service;


import chat.oriochat.app.dto.ChatMessageDTO;
import chat.oriochat.app.dto.ErrorResponse;
import chat.oriochat.app.model.Room;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SocketIOService {
    private final SocketIOServer server;
    private final Validator validator;

    @Autowired
    private RoomService roomService;

    public SocketIOService(SocketIOServer server, Validator validator) {
        this.server = server;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @PostConstruct
    public void startServer() {
        server.addConnectListener(this::onClientConnect);
        server.addDisconnectListener(this::onClientDisconnect);

        // Listener for joining a room
        server.addEventListener("join-to-room", ChatMessageDTO.class, this::onJoinRoom);

        // Listener for leave from a room
        server.addEventListener("leave-room", ChatMessageDTO.class, this::leaveFromRoom);

        // Send a message to the room
        server.addEventListener("send-message", ChatMessageDTO.class, this::sendMessageToRoom);

        // Listener for broadcasting a message to a specific room
        // server.addEventListener("broadcast-message", Message.class, onBroadcastMessage);
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket.IO server stopped.");
    }

    private void onClientConnect(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
    }

    private void onClientDisconnect(SocketIOClient client) {
        log.info("Client disconnected: {}", client.getSessionId());
    }

    // join to a specific room using room id and get welcome message
    private void onJoinRoom(SocketIOClient client, @Valid ChatMessageDTO messageDTO, Object ackSender) {
        // Validate the ChatMessageDTO
        Set<ConstraintViolation<ChatMessageDTO>> violations = validator.validate(messageDTO);
        if (!violations.isEmpty()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            for (ConstraintViolation<ChatMessageDTO> violation : violations) {
                String fieldName = violation.getPropertyPath().toString(); // Get the field name
                String errorMessage = violation.getMessage(); // Get the error message

                // Add the error message to the corresponding field
                errorMap.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            }

            // Create the ErrorResponse with the structured errors
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errorMap);

            // Send the structured validation errors back to the client
            client.sendEvent("error", errorResponse);
            return;
        }

        // Validate the room
        Optional<Room> availableRoom = roomService.getRoomByRoomId(messageDTO.getRoom());
        if (availableRoom.isEmpty()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            errorMap.computeIfAbsent("room", k -> new ArrayList<>()).add("Room not available. Please enter a valid room number.");

            // Create the ErrorResponse with the structured errors
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errorMap);

            // Send the structured validation errors back to the client
            client.sendEvent("error", errorResponse);
            return;
        }

        // Successfully joined to the room
        String roomId = messageDTO.getRoom();
        client.joinRoom(roomId);
        log.info("Client {} joined room: {}", client.getSessionId(), roomId);

        // Send a welcome message to the joiner
        ChatMessageDTO welcomeMessage = new ChatMessageDTO();
        welcomeMessage.setSender(messageDTO.getSender());
        welcomeMessage.setName(messageDTO.getName());
        welcomeMessage.setRoom(roomId);
        welcomeMessage.setType(ChatMessageDTO.MessageType.JOIN);
        String welcomeMessageContent = "Welcome to room " + availableRoom.get().getName() + "! Enjoy the chat.";
        welcomeMessage.setContent(welcomeMessageContent);
        client.sendEvent("message", welcomeMessage);

        // Notify other clients in the room that a new user has joined exclude new joiner
        ChatMessageDTO notifyMessage = new ChatMessageDTO();
        notifyMessage.setSender(messageDTO.getSender());
        notifyMessage.setName(messageDTO.getName());
        notifyMessage.setRoom(roomId);
        notifyMessage.setType(ChatMessageDTO.MessageType.JOIN);
        String messageContent = messageDTO.getName() + " has joined the room!";
        notifyMessage.setContent(messageContent);
        server.getRoomOperations(roomId).getClients().forEach(c -> {
            if (!c.getSessionId().equals(client.getSessionId())) {
                c.sendEvent("message", notifyMessage);
            }
        });
    }

    // Leave from a specific room
    public void leaveFromRoom(SocketIOClient client, @Valid ChatMessageDTO messageDTO, Object ackSender) {
        // Validate the ChatMessageDTO
        Set<ConstraintViolation<ChatMessageDTO>> violations = validator.validate(messageDTO);
        if (!violations.isEmpty()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            for (ConstraintViolation<ChatMessageDTO> violation : violations) {
                String fieldName = violation.getPropertyPath().toString(); // Get the field name
                String errorMessage = violation.getMessage(); // Get the error message

                // Add the error message to the corresponding field
                errorMap.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            }

            // Create the ErrorResponse with the structured errors
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errorMap);

            // Send the structured validation errors back to the client
            client.sendEvent("error", errorResponse);
            return;
        }

        // Validate the room
        Optional<Room> availableRoom = roomService.getRoomByRoomId(messageDTO.getRoom());
        if (availableRoom.isEmpty()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            errorMap.computeIfAbsent("room", k -> new ArrayList<>()).add("Room not available. Please enter a valid room number.");

            // Create the ErrorResponse with the structured errors
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errorMap);

            // Send the structured validation errors back to the client
            client.sendEvent("error", errorResponse);
            return;
        }

        // Successfully leave from the room
        String roomId = messageDTO.getRoom();
        client.leaveRoom(roomId);
        log.info("Client {} leaved room: {}", client.getSessionId(), roomId);

        // Notify other clients in the room that a new user has joined exclude new joiner
        ChatMessageDTO notifyMessage = new ChatMessageDTO();
        notifyMessage.setSender(messageDTO.getSender());
        notifyMessage.setName(messageDTO.getName());
        notifyMessage.setRoom(roomId);
        notifyMessage.setType(ChatMessageDTO.MessageType.LEAVE);
        String messageContent = messageDTO.getName() + " has leave the room!";
        notifyMessage.setContent(messageContent);
        server.getRoomOperations(roomId).getClients().forEach(c -> {
            if (!c.getSessionId().equals(client.getSessionId())) {
                c.sendEvent("message", notifyMessage);
            }
        });
    }

    // Send message to the room
    public void sendMessageToRoom(SocketIOClient client, @Valid ChatMessageDTO messageDTO, Object ackSender) {
        // Validate the ChatMessageDTO
        Set<ConstraintViolation<ChatMessageDTO>> violations = validator.validate(messageDTO);
        if (!violations.isEmpty()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            for (ConstraintViolation<ChatMessageDTO> violation : violations) {
                String fieldName = violation.getPropertyPath().toString(); // Get the field name
                String errorMessage = violation.getMessage(); // Get the error message

                // Add the error message to the corresponding field
                errorMap.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            }

            // Create the ErrorResponse with the structured errors
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errorMap);

            // Send the structured validation errors back to the client
            client.sendEvent("error", errorResponse);
            return;
        }

        // Validate the room
        Optional<Room> availableRoom = roomService.getRoomByRoomId(messageDTO.getRoom());

        if (availableRoom.isEmpty()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            errorMap.computeIfAbsent("room", k -> new ArrayList<>()).add("Room not available. Please enter a valid room number.");

            // Create the ErrorResponse with the structured errors
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errorMap);

            // Send the structured validation errors back to the client
            client.sendEvent("error", errorResponse);
            return;
        }

        // Successfully send message to the room
        String roomId = messageDTO.getRoom();

        // Notify other clients in the room that a new user has joined exclude new joiner
        ChatMessageDTO message = new ChatMessageDTO();
        message.setSender(messageDTO.getSender());
        message.setName(messageDTO.getName());
        message.setRoom(roomId);
        message.setType(ChatMessageDTO.MessageType.CHAT);
        message.setContent(messageDTO.getContent());
        server.getRoomOperations(roomId).sendEvent("message", message);
    }
}
