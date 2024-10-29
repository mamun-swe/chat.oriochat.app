package chat.oriochat.app.utility;

import java.util.Date;
import java.util.UUID;

public class Helpers {

    // generate unique room ID remove all non-alphanumeric characters
    public static String generateRoomNumber(String name) {
        String sanitizedName = name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return "room-" + UUID.randomUUID() + "-" + new Date().getTime() + "-" + sanitizedName;
    }
}
