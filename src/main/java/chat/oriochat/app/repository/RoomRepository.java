package chat.oriochat.app.repository;

import chat.oriochat.app.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {
    // Custom query method to find a room by name
    Optional<Room> findByName(String name);

    // Custom query method to find a room by room_id
    Optional<Room> findByRoomId(String roomId);

    // Custom query method to delete a room by room_id
    @Transactional
    void deleteByRoomId(String roomId);
}
