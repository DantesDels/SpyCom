package protocol;

public class Message {
    private final MessageType type;
    private final String pseudo;
    private final String contenu;
    private final long timestamp;
    private final String extra;
    private final boolean encrypted;

    public Message(MessageType type, String pseudo, String contenu, long timestamp) {
        this(type, pseudo, contenu, timestamp, "", false);
    }

    public Message(MessageType type, String pseudo, String contenu, long timestamp, String extra) {
        this(type, pseudo, contenu, timestamp, extra, false);
    }

    public Message(MessageType type, String pseudo, String contenu, long timestamp, String extra, boolean encrypted) {
        this.type = type;
        this.pseudo = pseudo;
        this.contenu = contenu;
        this.timestamp = timestamp;
        this.extra = extra;
        this.encrypted = encrypted;
    }

    public MessageType getType() { return type; }
    public String getPseudo() { return pseudo; }
    public String getContenu() { return contenu; }
    public long getTimestamp() { return timestamp; }
    public String getExtra() { return extra; }
    public boolean isEncrypted() { return encrypted; }
}
