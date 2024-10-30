package chat.oriochat.app.repository;

import chat.oriochat.app.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findMessagesByRoom(String room);
}
