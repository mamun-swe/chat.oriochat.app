package chat.oriochat.app.repository;

import chat.oriochat.app.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findMessagesByRoomId(String roomId);
}
