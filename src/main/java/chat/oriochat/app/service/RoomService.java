package chat.oriochat.app.service;


import chat.oriochat.app.dto.RoomDTO;
import chat.oriochat.app.model.Room;
import chat.oriochat.app.repository.RoomRepository;
import chat.oriochat.app.utility.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    // Get list of the rooms
    public List<Room> getRooms() {
        return roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // Create a new room
    public void createRoom(RoomDTO roomDTO) {
        String roomNumber = Helpers.generateRoomNumber(roomDTO.getName());

        Room room = new Room();
        room.setName(roomDTO.getName());
        room.setRoomId(roomNumber);
        roomRepository.save(room);
    }

    // Get specific room by id
    public Optional<Room> getRoom(Long id) {
        return roomRepository.findById(id);
    }

    // Get specific room by name
    public Optional<Room> getRoom(String name) {
        return roomRepository.findByName(name);
    }

    // Get specific room by room_id
    public Optional<Room> getRoomByRoomId(String roomId) {
        return roomRepository.findByRoomId(roomId);
    }

    // Delete specific room by id
    public void destroyRoom(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            roomRepository.deleteById(id);
        }

    }
}
