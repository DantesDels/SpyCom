package ui.net;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import protocol.Message;
import protocol.MessageType;

/**
 * Manages the incoming half of file transfers:
 * accepting FILE_META / FILE_DATA messages, accumulating
 * base-64 chunks, and triggering reassembly + save when complete.
 */
public class FileReceiver {

    private final Consumer<Message> addMessage;
    private final Map<String, Transfer> pending = new ConcurrentHashMap<>();

    public FileReceiver(Consumer<Message> addMessage) {
        this.addMessage = addMessage;
    }

    /** Register a new incoming file transfer from FILE_META. */
    public void onMeta(Message m) {
        String meta = m.getContenu();
        if (meta == null) return;
        String[] p = meta.split(":", 2);
        if (p.length < 2) return;
        pending.put(m.getPseudo() + ":" + p[0], new Transfer(m.getPseudo(), p[0]));
        addMessage.accept(new Message(MessageType.SERVER_INFO, null,
            "\uD83D\uDCCE  Reception de \"" + p[0] + "\" de " + m.getPseudo(), System.currentTimeMillis()));
    }

    /**
     * Accumulate a base-64 chunk from FILE_DATA.
     * When all chunks have arrived, reassemble, save, and push a FILE_COMPLETE_*.
     */
    public void onData(Message m) {
        String extra = m.getExtra();
        if (extra == null) return;

        String[] meta = extra.split(":", 3);
        if (meta.length < 3) return;

        int idx = Integer.parseInt(meta[0]);
        int total = Integer.parseInt(meta[1]);
        String name = meta[2];
        String key = m.getPseudo() + ":" + name;

        Transfer t = pending.get(key);
        if (t == null) {
            t = new Transfer(m.getPseudo(), name);
            pending.put(key, t);
        }
        if (t.total == 0) t.total = total;
        t.chunks.put(idx, m.getContenu());
        if (!t.done()) return;

        try {
            byte[] data = t.assemble();
            FileSaver.saveAndNotify(m.getPseudo(), name, data, addMessage);
        } catch (Exception e) {
            addMessage.accept(new Message(MessageType.SERVER_INFO, null,
                "\u274C  Erreur reception " + name, System.currentTimeMillis()));
        }
        pending.remove(key);
    }

    /** State for one in-progress file download. */
    private static class Transfer {
        final String sender, filename;
        int total;
        final Map<Integer, String> chunks = new ConcurrentHashMap<>();
        Transfer(String s, String f) { this.sender = s; this.filename = f; }
        boolean done() { return total > 0 && chunks.size() == total; }

        byte[] assemble() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < total; i++) {
                String c = chunks.get(i);
                if (c != null) sb.append(c);
            }
            return Base64.getDecoder().decode(sb.toString());
        }
    }
}
