package chat.oriochat.app.service;

import chat.oriochat.app.dto.ChatMessageDTO;
import chat.oriochat.app.model.ChatMessage;
import chat.oriochat.app.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatRepository chatRepository;

    // Get list of messages
    public List<ChatMessage> getMessages(String room) {
        return chatRepository.findMessagesByRoom(room);
    }

    // Create a new message
    public void createMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.CHAT);
        message.setSenderId(chatMessageDTO.getSenderId());
        message.setSenderName(chatMessageDTO.getSenderName());
        message.setRoom(chatMessageDTO.getRoom());
        message.setContent(chatMessageDTO.getContent());
        chatRepository.save(message);
    }

    // Delete all messages
    public void deleteMessages() {
        chatRepository.deleteAll();
    }
}
