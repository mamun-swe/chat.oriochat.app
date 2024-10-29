package chat.oriochat.app.controller;

import chat.oriochat.app.dto.ErrorResponse;
import chat.oriochat.app.dto.RoomDTO;
import chat.oriochat.app.dto.SuccessResponse;
import chat.oriochat.app.model.Room;
import chat.oriochat.app.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<Room>>> getRooms() {
        List<Room> rooms = roomService.getRooms();
        return ResponseEntity.ok(new SuccessResponse<>(true, "List of the rooms.", rooms));
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        // If the name already exists, return a custom error response
        Optional<Room> availableRoom = roomService.getRoom(roomDTO.getName());
        if (availableRoom.isPresent()) {
            Map<String, String[]> errors = new HashMap<>();
            errors.put("name", new String[]{"This room already exists."});
            ErrorResponse<Map<String, String[]>> errorResponse = new ErrorResponse<>("Found existing data.", errors);
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        roomService.createRoom(roomDTO);
        return ResponseEntity.ok(new SuccessResponse<>(true, "Successfully room created.", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<?>> getRoom(@PathVariable Long id) {
        Optional<Room> availableRoom = roomService.getRoom(id);
        return ResponseEntity.ok(new SuccessResponse<>(true, "Room information.", availableRoom));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> destroyRoom(@PathVariable Long id) {
        // If the name already exists, return a custom error response
        Optional<Room> availableRoom = roomService.getRoom(id);
        if (availableRoom.isPresent()) {
            roomService.destroyRoom(id);
            return ResponseEntity.ok(new SuccessResponse<>(true, "Successfully room deleted.", null));
        }

        Map<String, String[]> errors = new HashMap<>();
        errors.put("room", new String[]{"This room isn't available."});
        ErrorResponse<Map<String, String[]>> errorResponse = new ErrorResponse<>("Data not found.", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
