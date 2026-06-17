package client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.function.Consumer;
import protocol.Crypto;
import protocol.Message;
import protocol.MessageSerializer;
import protocol.MessageType;

public class ChatClient {
    private final String host;
    private final int port;
    private String pseudo;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageReceiver receiver;
    private Thread receiverThread;
    private Consumer<Message> callback;

    public ChatClient(String host, int port, String pseudo) {
        this.host = host; this.port = port; this.pseudo = pseudo;
    }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String p) { this.pseudo = p; }
    public String getHost() { return host; }
    public int getPort() { return port; }

    public void setCallback(Consumer<Message> cb) { this.callback = cb; }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(MessageSerializer.serialize(
            new Message(MessageType.CONNECT, pseudo, null, System.currentTimeMillis())));
        receiver = new MessageReceiver(in, callback);
        receiverThread = new Thread(receiver, "MessageReceiver");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private void send(String msg) { if (out != null) out.println(msg); }

    public void sendText(String text) {
        send(MessageSerializer.serialize(
            new Message(MessageType.TEXT, pseudo, Crypto.encrypt(text),
                System.currentTimeMillis(), "", true)));
    }

    public void sendPrivateMessage(String target, String text) {
        send(MessageSerializer.serialize(
            new Message(MessageType.PRIVATE_MSG, pseudo, Crypto.encrypt(text),
                System.currentTimeMillis(), target, true)));
    }

    public void joinRoom(String room) { joinRoom(room, true); }

    public void joinRoom(String room, boolean isPublic) {
        send(MessageSerializer.serialize(
            new Message(MessageType.JOIN_ROOM, pseudo, room,
                System.currentTimeMillis(), isPublic ? "" : "private")));
    }

    public void changeStatus(String s) {
        send(MessageSerializer.serialize(
            new Message(MessageType.STATUS_CHANGE, pseudo, s, System.currentTimeMillis())));
    }

    public void changeNick(String newPseudo) {
        send(MessageSerializer.serialize(
            new Message(MessageType.NICK_CHANGE, pseudo, newPseudo, System.currentTimeMillis())));
    }

    public void requestRoomList() {
        send(MessageSerializer.serialize(
            new Message(MessageType.ROOM_LIST_REQ, pseudo, null, System.currentTimeMillis())));
    }

    public void inviteUser(String target) {
        send(MessageSerializer.serialize(
            new Message(MessageType.INVITE_USER, pseudo, target, System.currentTimeMillis())));
    }

    public void sendFile(Path filePath) {
        try {
            byte[] data = Files.readAllBytes(filePath);
            String name = filePath.getFileName().toString();
            String b64 = Base64.getEncoder().encodeToString(data);
            send(MessageSerializer.serialize(
                new Message(MessageType.FILE_META, pseudo, name + ":" + data.length, System.currentTimeMillis())));
            int cs = 4000, total = (int) Math.ceil((double) b64.length() / cs);
            for (int i = 0; i < total; i++) {
                int end = Math.min((i + 1) * cs, b64.length());
                send(MessageSerializer.serialize(
                    new Message(MessageType.FILE_DATA, pseudo, b64.substring(i * cs, end),
                        System.currentTimeMillis(), i + ":" + total + ":" + name)));
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] Erreur envoi fichier: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try { send(MessageSerializer.serialize(
                new Message(MessageType.DISCONNECT, pseudo, null, System.currentTimeMillis()))); } catch (Exception e) {}
            if (receiver != null) receiver.stop();
            try { socket.close(); } catch (IOException e) {}
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
