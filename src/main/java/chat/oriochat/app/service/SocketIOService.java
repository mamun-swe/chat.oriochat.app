package chat.oriochat.app.service;

import chat.oriochat.app.dto.ChatMessageDTO;
import chat.oriochat.app.dto.ErrorResponse;
import chat.oriochat.app.exception.ChatMessageValidator;
import chat.oriochat.app.model.ChatMessage;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SocketIOService {
    private final SocketIOServer server;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatMessageValidator chatMessageValidator;

    List<ChatMessageDTO> chatMessages = new ArrayList<>(); /* Declare global message array */

    public SocketIOService(SocketIOServer server) {
        this.server = server;
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
        Map<String, List<String>> errors = chatMessageValidator.validateChatMessage(messageDTO);
        if (!errors.isEmpty()) {
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errors);
            client.sendEvent("error", errorResponse);
            return;
        }

        // Validate the room
        Map<String, List<String>> roomErrors = chatMessageValidator.validateRoom(messageDTO.getRoom());
        if (!roomErrors.isEmpty()) {
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", roomErrors);
            client.sendEvent("error", errorResponse);
            return;
        }

        // Successfully joined to the room
        String roomId = messageDTO.getRoom();
        client.joinRoom(roomId);
        log.info("Client {} joined room: {}", client.getSessionId(), roomId);

        List<ChatMessage> oldMessages = chatMessageService.getMessages(roomId);
        List<ChatMessageDTO> oldMessagesDTO = oldMessages.stream().map(message -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setId(message.getId());
            dto.setSenderId(message.getSenderId());
            dto.setSenderName(message.getSenderName());
            dto.setRoom(message.getRoom());
            dto.setType(message.getType());
            dto.setContent(message.getContent());
            return dto;
        }).toList();

        chatMessages.addAll(oldMessagesDTO);

        // Send a welcome message to the joiner
        ChatMessageDTO welcomeMessage = new ChatMessageDTO();
        welcomeMessage.setSenderId(messageDTO.getSenderId());
        welcomeMessage.setSenderName(messageDTO.getSenderName());
        welcomeMessage.setRoom(roomId);
        welcomeMessage.setType(ChatMessage.MessageType.JOIN);
        welcomeMessage.setContent("Welcome to room! Enjoy the chat.");
        chatMessages.addLast(welcomeMessage);

        // Broadcast welcome message to the new joiner
        client.sendEvent("messages", chatMessages);

        // Notify other clients in the room that a new user has joined exclude new joiner
        ChatMessageDTO notifyMessage = new ChatMessageDTO();
        notifyMessage.setSenderId(messageDTO.getSenderId());
        notifyMessage.setSenderName(messageDTO.getSenderName());
        notifyMessage.setRoom(roomId);
        notifyMessage.setType(ChatMessage.MessageType.JOIN);
        notifyMessage.setContent(messageDTO.getSenderName() + " has joined the room!");
        chatMessages.clear();
        chatMessages.add(notifyMessage);

        server.getRoomOperations(roomId).getClients().forEach(c -> {
            if (!c.getSessionId().equals(client.getSessionId())) {
                c.sendEvent("messages", chatMessages);
            }
        });
    }

    // Leave from a specific room
    public void leaveFromRoom(SocketIOClient client, @Valid ChatMessageDTO messageDTO, Object ackSender) {
        // Validate the ChatMessageDTO
        Map<String, List<String>> errors = chatMessageValidator.validateChatMessage(messageDTO);
        if (!errors.isEmpty()) {
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errors);
            client.sendEvent("error", errorResponse);
            return;
        }

        // Validate the room
        Map<String, List<String>> roomErrors = chatMessageValidator.validateRoom(messageDTO.getRoom());
        if (!roomErrors.isEmpty()) {
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", roomErrors);
            client.sendEvent("error", errorResponse);
            return;
        }

        // Successfully leave from the room
        String roomId = messageDTO.getRoom();
        client.leaveRoom(roomId);
        log.info("Client {} leaved room: {}", client.getSessionId(), roomId);

        // Notify other clients in the room that a new user has joined exclude new joiner
        ChatMessageDTO notifyMessage = new ChatMessageDTO();
        notifyMessage.setSenderId(messageDTO.getSenderId());
        notifyMessage.setSenderName(messageDTO.getSenderName());
        notifyMessage.setRoom(roomId);
        notifyMessage.setType(ChatMessage.MessageType.LEAVE);
        notifyMessage.setContent(messageDTO.getSenderName() + " has leave the room!");

        chatMessages.clear();
        chatMessages.add(notifyMessage);

        // Broadcast messages to the joiner exclude leave joiner
        server.getRoomOperations(roomId).getClients().forEach(c -> {
            if (!c.getSessionId().equals(client.getSessionId())) {
                c.sendEvent("messages", chatMessages);
            }
        });
    }

    // Send message to the room
    public void sendMessageToRoom(SocketIOClient client, @Valid ChatMessageDTO messageDTO, Object ackSender) {
        // Validate the ChatMessageDTO
        Map<String, List<String>> errors = chatMessageValidator.validateChatMessage(messageDTO);
        if (!errors.isEmpty()) {
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", errors);
            client.sendEvent("error", errorResponse);
            return;
        }

        // Validate the room
        Map<String, List<String>> roomErrors = chatMessageValidator.validateRoom(messageDTO.getRoom());
        if (!roomErrors.isEmpty()) {
            ErrorResponse<Map<String, List<String>>> errorResponse = new ErrorResponse<>("Validation errors occurred!", roomErrors);
            client.sendEvent("error", errorResponse);
            return;
        }

        // Successfully send message to the room
        String roomId = messageDTO.getRoom();

        // Notify other clients in the room that a new user has joined exclude new joiner
        ChatMessageDTO message = new ChatMessageDTO();
        message.setSenderId(messageDTO.getSenderId());
        message.setSenderName(messageDTO.getSenderName());
        message.setRoom(roomId);
        message.setType(ChatMessage.MessageType.CHAT);
        message.setContent(messageDTO.getContent());

        // Store message to the database
        chatMessageService.createMessage(message);

        // Add message to the messages array
        chatMessages.clear();
        chatMessages.add(message);

        // Broadcast messages to the room
        server.getRoomOperations(roomId).sendEvent("messages", chatMessages);
    }
}
