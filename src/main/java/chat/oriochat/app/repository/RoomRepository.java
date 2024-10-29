package chat.oriochat.app.repository;

import chat.oriochat.app.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // Custom query method to find a room by name
    Optional<Room> findByName(String name);

    // Custom query method to find a room by room_id
    Optional<Room> findByRoomId(String roomId);
}
