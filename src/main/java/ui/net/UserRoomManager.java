package ui.net;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javafx.collections.ObservableList;

/**
 * Parses USER_LIST and ROOM_LIST protocol messages into
 * ObservableList entries that the UI ListViews observe directly.
 *
 * Wire format
 * -----------
 *   USER_LIST content = "pseudo1:status1,pseudo2:status2|totalCount"
 *   ROOM_LIST content = "name1:count1:type1,name2:count2:type2"
 */
public class UserRoomManager {

    private final ObservableList<String> users;
    private final ObservableList<String> rooms;
    private final Consumer<String> userCountSetter;
    private final IntConsumer totalAgentsSetter;

    public UserRoomManager(ObservableList<String> users, ObservableList<String> rooms,
                           Consumer<String> userCountSetter, IntConsumer totalAgentsSetter) {
        this.users = users;
        this.rooms = rooms;
        this.userCountSetter = userCountSetter;
        this.totalAgentsSetter = totalAgentsSetter;
    }

    /**
     * Parse "pseudo:status,pseudo:status|totalCount".
     * Each entry becomes "pseudo|status" so UserCell can render coloured dots.
     */
    public void updateUsers(String raw) {
        users.clear();
        if (raw == null || raw.isEmpty()) return;

        String[] parts = raw.split("\\|", 2);
        if (parts.length > 1) {
            try { totalAgentsSetter.accept(Integer.parseInt(parts[1])); } catch (NumberFormatException e) { /* ignore */ }
        }
        for (String entry : parts[0].split(",")) {
            if (entry.isEmpty()) continue;
            String[] kv = entry.split(":", 2);
            users.add(kv[0] + "|" + (kv.length > 1 ? kv[1] : "online"));
        }
        userCountSetter.accept(users.size() + " en ligne");
    }

    /**
     * Parse "name:count:type,name:count:type".
     * Public rooms get a green icon, private rooms a lock icon.
     */
    public void updateRooms(String raw) {
        rooms.clear();
        if (raw == null || raw.isEmpty()) return;

        for (String entry : raw.split(",")) {
            if (entry.isEmpty()) continue;
            String[] kv = entry.split(":", 3);
            // kv[0]=name  kv[1]=count  kv[2]=type (optional, "private" or absent)
            boolean priv = kv.length > 2 && "private".equals(kv[2]);
            rooms.add((priv ? "\uD83D\uDD12" : "\uD83D\uDD10") + " " + kv[0] + " (" + (kv.length > 1 ? kv[1] : "0") + ")");
        }
    }
}
