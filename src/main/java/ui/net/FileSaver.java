package ui.net;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import client.ChatClient;
import protocol.Message;
import protocol.MessageType;

/**
 * Writes received or locally-sent files to ~/chat_downloads/
 * and pushes the appropriate FILE_COMPLETE_* message into the chat.
 *
 * Handles duplicate filenames by appending _1, _2, … before the extension.
 */
public final class FileSaver {

    private static final Path DL_DIR = Paths.get(System.getProperty("user.home"), "chat_downloads");

    static {
        try { Files.createDirectories(DL_DIR); } catch (IOException e) { /* best-effort */ }
    }

    /** Save a local file when the current user sends it. */
    public static void saveLocal(ChatClient client, Path source, Consumer<Message> addMessage) {
        String name = source.getFileName().toString();
        try {
            Path dest = makeUnique(DL_DIR.resolve(name));
            Files.copy(source, dest);
            byte[] data = Files.readAllBytes(dest);
            pushFileMessage(client.getPseudo(), dest, name, data, addMessage);
        } catch (IOException e) {
            addMessage.accept(new Message(MessageType.SERVER_INFO, null,
                "\u274C  Erreur sauvegarde locale du fichier", System.currentTimeMillis()));
        }
    }

    /** Save reassembled bytes from a remote sender. */
    static void saveAndNotify(String pseudo, String name, byte[] data, Consumer<Message> addMessage) {
        try {
            Path dest = makeUnique(DL_DIR.resolve(name));
            Files.write(dest, data);
            pushFileMessage(pseudo, dest, name, data, addMessage);
        } catch (IOException e) {
            addMessage.accept(new Message(MessageType.SERVER_INFO, null,
                "\u274C  Erreur ecriture " + name, System.currentTimeMillis()));
        }
    }

    // ── File-type detection ─────────────────────────────────

    public static boolean isImage(String name) {
        return name.toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)$");
    }

    public static boolean isTextFile(String name) {
        return name.toLowerCase().matches(".*\\.(txt|md|java|py|js|html|css|json|xml|log|csv)$");
    }

    // ── Internal helpers ────────────────────────────────────

    private static void pushFileMessage(String pseudo, Path path, String name, byte[] data, Consumer<Message> addMessage) {
        if (isImage(name))
            addMessage.accept(new Message(MessageType.FILE_COMPLETE_IMG, pseudo, path.toString(), System.currentTimeMillis(), name));
        else if (isTextFile(name)) {
            String prev = data.length > 2000
                ? new String(data, 0, 2000, StandardCharsets.UTF_8) + "\n... [truncated]"
                : new String(data, StandardCharsets.UTF_8);
            addMessage.accept(new Message(MessageType.FILE_COMPLETE_TXT, pseudo, prev, System.currentTimeMillis(), path + "|" + name));
        } else
            addMessage.accept(new Message(MessageType.FILE_COMPLETE_OTHER, pseudo, path.toString(), System.currentTimeMillis(), name));
    }

    private static Path makeUnique(Path dest) {
        if (!Files.exists(dest)) return dest;
        String name = dest.getFileName().toString();
        String base = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.')) : "";
        Path parent = dest.getParent();
        int n = 1;
        while (Files.exists(parent.resolve(base + "_" + n + ext))) n++;
        return parent.resolve(base + "_" + n + ext);
    }

    private FileSaver() {}
}
