package protocol;

public class MessageSerializer {

    private static void debug(String msg) {
        System.out.println("[SERIALIZER-DEBUG] " + msg);
    }

    public static String serialize(Message msg) {
        String pseudo = escape(msg.getPseudo() != null ? msg.getPseudo() : "");
        String contenu = escape(msg.getContenu() != null ? msg.getContenu() : "");
        String extra = escape(msg.getExtra() != null ? msg.getExtra() : "");
        String enc = msg.isEncrypted() ? "1" : "0";
        String result = String.format("%s|%s|%s|%d|%s|%s",
            msg.getType().name(), pseudo, contenu, msg.getTimestamp(), extra, enc);
        debug("Serialize: " + result);
        return result;
    }

    public static Message deserialize(String line) {
        if (line == null || line.isBlank()) {
            debug("Deserialize: ligne vide ou nulle");
            return null;
        }
        try {
            String[] parts = line.split("\\|", 6);
            if (parts.length < 1) {
                debug("Deserialize: pas assez de parties");
                return null;
            }
            MessageType type = MessageType.valueOf(parts[0]);
            String pseudo = parts.length > 1 && !parts[1].isEmpty() ? unescape(parts[1]) : null;
            String contenu = parts.length > 2 && !parts[2].isEmpty() ? unescape(parts[2]) : null;
            long timestamp = parts.length > 3 ? Long.parseLong(parts[3]) : System.currentTimeMillis();
            String extra = parts.length > 4 && !parts[4].isEmpty() ? unescape(parts[4]) : "";
            boolean encrypted = parts.length > 5 && "1".equals(parts[5]);
            debug("Deserialize OK: type=" + type + " pseudo=" + pseudo);
            return new Message(type, pseudo, contenu, timestamp, extra, encrypted);
        } catch (IllegalArgumentException e) {
            debug("Deserialize erreur: " + e.getMessage());
            return null;
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\\\", "\\").replace("\\n", "\n").replace("\\p", "|");
    }
}
