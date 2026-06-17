package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import protocol.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ClientRegistry registry;
    private final PrintWriter out;
    private final BufferedReader in;
    private volatile String pseudo;
    private volatile String room = "general";
    private volatile String status = "online";
    private final Map<String, FileTransfer> pendingFileSaves = new ConcurrentHashMap<>();

    private static class FileTransfer {
        String filename;
        int totalChunks;
        final Map<Integer, String> chunks = new ConcurrentHashMap<>();
        boolean isComplete() { return totalChunks > 0 && chunks.size() == totalChunks; }
        byte[] assemble() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < totalChunks; i++) {
                String c = chunks.get(i);
                if (c != null) sb.append(c);
            }
            return Base64.getDecoder().decode(sb.toString());
        }
    }

    public ClientHandler(Socket socket, ClientRegistry registry) throws IOException {
        this.socket = socket;
        this.registry = registry;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void debug(String msg) {
        System.out.println("[HANDLER-DEBUG][" + (pseudo != null ? pseudo : "?") + "] " + msg);
    }

    public String getPseudo() { return pseudo; }
    public String getRoom() { return room; }
    public String getStatus() { return status; }
    public void setRoom(String room) { this.room = room; }
    public void setStatus(String status) { this.status = status; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public void send(String message) { out.println(message); }

    @Override
    public void run() {
        debug("Thread demarre");
        try {
            String line = in.readLine();
            if (line == null) return;
            Message msg = MessageSerializer.deserialize(line);
            if (msg == null || msg.getType() != MessageType.CONNECT) return;

            this.pseudo = msg.getPseudo();
            if (this.pseudo == null || this.pseudo.isBlank()) return;

            if (!registry.register(this, this.pseudo)) {
                send(MessageSerializer.serialize(new Message(MessageType.PSEUDO_TAKEN, null,
                    "Pseudo \"" + this.pseudo + "\" deja utilise", System.currentTimeMillis())));
                return;
            }

            registry.sendUserList(this, "general");
            registry.sendHistory(this, "general");
            registry.broadcastToAll(new Message(MessageType.SERVER_INFO, null,
                pseudo + " a rejoint le chat", System.currentTimeMillis()));
            registry.broadcastUserList("general");

            while ((line = in.readLine()) != null) {
                msg = MessageSerializer.deserialize(line);
                if (msg == null) continue;

                switch (msg.getType()) {
                    case TEXT -> {
                        Message tm = new Message(MessageType.TEXT, pseudo, msg.getContenu(),
                            msg.getTimestamp(), room, msg.isEncrypted());
                        registry.addHistory(tm, room);
                        registry.broadcastToRoom(tm, room, this);
                    }
                    case PRIVATE_MSG -> {
                        String target = msg.getExtra();
                        Message pm = new Message(MessageType.PRIVATE_MSG, pseudo,
                            msg.getContenu(), msg.getTimestamp(), target, msg.isEncrypted());
                        registry.sendToClient(target, pm);
                        send(MessageSerializer.serialize(new Message(MessageType.SERVER_INFO, null,
                            "Message prive envoye a " + target, System.currentTimeMillis())));
                    }
                    case JOIN_ROOM -> {
                        String newRoom = msg.getContenu();
                        if (newRoom == null || newRoom.isBlank() || newRoom.equals(room)) break;
                        boolean isPublic = !"private".equals(msg.getExtra());
                        String oldRoom = this.room;
                        registry.leaveRoom(this, oldRoom);
                        registry.joinRoom(this, newRoom, isPublic);
                        if (!this.room.equals(newRoom)) break;
                        registry.broadcastUserList(oldRoom);
                        registry.broadcastUserList(newRoom);
                        registry.broadcastToRoom(new Message(MessageType.SERVER_INFO, null,
                            pseudo + " a rejoint le salon", System.currentTimeMillis()), newRoom, null);
                        registry.sendHistory(this, newRoom);
                    }
                    case STATUS_CHANGE -> {
                        this.status = msg.getContenu();
                        registry.broadcastUserList(room);
                    }
                    case NICK_CHANGE -> {
                        String newP = msg.getContenu();
                        if (newP == null || newP.isBlank()) break;
                        String oldP = this.pseudo;
                        if (registry.changePseudo(this, newP)) {
                            send(MessageSerializer.serialize(
                                new Message(MessageType.NICK_CHANGE, null, newP, System.currentTimeMillis())));
                            registry.broadcastToAll(new Message(MessageType.SERVER_INFO, null,
                                oldP + " s'appelle maintenant " + newP, System.currentTimeMillis()));
                            registry.broadcastUserList(room);
                        } else {
                            send(MessageSerializer.serialize(
                                new Message(MessageType.SERVER_INFO, null,
                                    "Pseudo \"" + newP + "\" deja utilise", System.currentTimeMillis())));
                        }
                    }
                    case ROOM_LIST_REQ -> {
                        registry.broadcastRoomList();
                    }
                    case INVITE_USER -> {
                        String target = msg.getContenu();
                        registry.addInvitation(this.room, target);
                    }
                    case FILE_META -> {
                        String meta = msg.getContenu();
                        if (meta == null) break;
                        String[] parts = meta.split(":", 2);
                        String fname = parts[0];
                        String key = pseudo + ":" + fname;
                        FileTransfer ft = new FileTransfer();
                        ft.filename = fname;
                        pendingFileSaves.put(key, ft);

                        Message m = new Message(MessageType.FILE_META, pseudo, meta,
                            msg.getTimestamp(), room);
                        registry.addHistory(m, room);
                        registry.broadcastToRoom(m, room, this);
                    }
                    case FILE_DATA -> {
                        String extra = msg.getExtra();
                        if (extra == null) break;
                        String[] meta = extra.split(":", 3);
                        if (meta.length < 3) break;
                        int idx = Integer.parseInt(meta[0]);
                        int total = Integer.parseInt(meta[1]);
                        String fname = meta[2];
                        String key = pseudo + ":" + fname;
                        FileTransfer ft = pendingFileSaves.get(key);
                        if (ft == null) {
                            ft = new FileTransfer();
                            ft.filename = fname;
                            pendingFileSaves.put(key, ft);
                        }
                        ft.totalChunks = total;
                        ft.chunks.put(idx, msg.getContenu());

                        Message dm = new Message(MessageType.FILE_DATA, pseudo,
                            msg.getContenu(), msg.getTimestamp(), msg.getExtra());
                        registry.addHistory(dm, room);
                        registry.broadcastToRoom(dm, room, this);

                        if (ft.isComplete()) {
                            try {
                                byte[] data = ft.assemble();
                                Files.createDirectories(ClientRegistry.FILES_DIR);
                                Path dest = ClientRegistry.FILES_DIR.resolve(fname);
                                Path unique = dest;
                                int cnt = 1;
                                while (Files.exists(unique)) {
                                    String n = fname.contains(".") ? fname.substring(0, fname.lastIndexOf('.')) : fname;
                                    String e = fname.contains(".") ? fname.substring(fname.lastIndexOf('.')) : "";
                                    unique = ClientRegistry.FILES_DIR.resolve(n + "_" + cnt + e);
                                    cnt++;
                                }
                                Files.write(unique, data);
                                debug("Fichier sauvegarde sur le serveur: " + unique);
                                pendingFileSaves.remove(key);
                            } catch (IOException e) {
                                debug("Erreur sauvegarde fichier: " + e.getMessage());
                            }
                        }
                    }
                    case DISCONNECT -> { return; }
                }
            }
        } catch (IOException e) {
            debug("IOException: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        String oldRoom = this.room;
        registry.unregister(this);
        if (pseudo != null) {
            registry.broadcastToAll(new Message(MessageType.SERVER_INFO, null,
                pseudo + " a quitte le chat", System.currentTimeMillis()));
            registry.broadcastUserList(oldRoom);
        }
        try { socket.close(); } catch (IOException e) {}
    }
}
