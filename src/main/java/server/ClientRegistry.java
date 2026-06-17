package server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import protocol.Message;
import protocol.MessageSerializer;
import protocol.MessageType;

public class ClientRegistry {
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<ClientHandler>> rooms = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<Message>> history = new ConcurrentHashMap<>();
    private final Map<String, Boolean> roomVisibility = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArraySet<String>> roomInvitations = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY = 100;
    static final Path FILES_DIR = Paths.get(System.getProperty("user.dir"), "server_files");

    private void debug(String msg) {
        System.out.println("[REGISTRY-DEBUG] " + msg);
    }

    public boolean isPseudoTaken(String pseudo) {
        return clients.containsKey(pseudo);
    }

    public synchronized boolean register(ClientHandler handler, String pseudo) {
        if (clients.containsKey(pseudo)) {
            debug("Pseudo deja pris: " + pseudo);
            return false;
        }
        clients.put(pseudo, handler);
        joinRoom(handler, "general", true);
        debug("Client enregistre: " + pseudo + " (total: " + clients.size() + ")");
        return true;
    }

    public synchronized boolean changePseudo(ClientHandler handler, String newPseudo) {
        String oldPseudo = handler.getPseudo();
        if (oldPseudo == null || clients.containsKey(newPseudo)) return false;
        clients.remove(oldPseudo);
        clients.put(newPseudo, handler);
        handler.setPseudo(newPseudo);
        return true;
    }

    public void unregister(ClientHandler handler) {
        String pseudo = handler.getPseudo();
        if (pseudo != null) {
            clients.remove(pseudo);
            debug("Client retire: " + pseudo + " (total: " + clients.size() + ")");
        }
        for (String room : List.copyOf(rooms.keySet())) {
            leaveRoom(handler, room);
        }
    }

    public synchronized void joinRoom(ClientHandler handler, String room, boolean isPublic) {
        String oldRoom = handler.getRoom();
        if (oldRoom != null && !oldRoom.isEmpty() && !oldRoom.equals(room)) {
            leaveRoom(handler, oldRoom);
        }
        boolean exists = rooms.containsKey(room);
        if (!exists) {
            roomVisibility.put(room, isPublic);
        }
        boolean isPrivate = !roomVisibility.getOrDefault(room, true);
        if (isPrivate && !room.equals("general")) {
            CopyOnWriteArraySet<String> invited = roomInvitations.get(room);
            String pseudo = handler.getPseudo();
            if (invited == null || !invited.contains(pseudo)) {
                if (!roomInvitations.computeIfAbsent(room, k -> new CopyOnWriteArraySet<>()).contains(pseudo)) {
                    debug("Acces refuse au salon prive: " + room + " pour " + pseudo);
                    handler.send(MessageSerializer.serialize(
                        new Message(MessageType.SERVER_INFO, null,
                            "Salon prive \"" + room + "\" - vous n'etes pas invite",
                            System.currentTimeMillis())));
                    return;
                }
            }
        }
        rooms.computeIfAbsent(room, k -> new CopyOnWriteArrayList<>()).add(handler);
        handler.setRoom(room);
        broadcastRoomList();
        debug(handler.getPseudo() + " a rejoint le salon " + room + " (public=" + isPublic + ")");
    }

    public void leaveRoom(ClientHandler handler, String room) {
        CopyOnWriteArrayList<ClientHandler> list = rooms.get(room);
        if (list != null) {
            list.remove(handler);
            if (list.isEmpty()) {
                rooms.remove(room);
                roomVisibility.remove(room);
                roomInvitations.remove(room);
                history.remove(room);
                debug("Salon supprime (vide): " + room);
                broadcastRoomList();
            }
        }
    }

    public boolean isRoomPublic(String room) {
        return roomVisibility.getOrDefault(room, true);
    }

    public void addInvitation(String room, String targetPseudo) {
        roomInvitations.computeIfAbsent(room, k -> new CopyOnWriteArraySet<>()).add(targetPseudo);
        ClientHandler target = clients.get(targetPseudo);
        if (target != null) {
            target.send(MessageSerializer.serialize(
                new Message(MessageType.SERVER_INFO, null,
                    "Vous avez ete invite au salon \"" + room + "\". Tapez /join " + room,
                    System.currentTimeMillis())));
        }
    }

    public void broadcastToRoom(Message message, String room, ClientHandler sender) {
        String serialized = MessageSerializer.serialize(message);
        CopyOnWriteArrayList<ClientHandler> list = rooms.get(room);
        if (list == null) return;
        for (ClientHandler client : list) {
            if (client != sender) client.send(serialized);
        }
    }

    public void broadcastToAll(Message message) {
        String serialized = MessageSerializer.serialize(message);
        for (ClientHandler client : clients.values()) {
            client.send(serialized);
        }
    }

    public void sendToClient(String pseudo, Message message) {
        ClientHandler target = clients.get(pseudo);
        if (target != null)
            target.send(MessageSerializer.serialize(message));
    }

    public void addHistory(Message message, String room) {
        CopyOnWriteArrayList<Message> list = history.computeIfAbsent(room, k -> new CopyOnWriteArrayList<>());
        list.add(message);
        if (list.size() > MAX_HISTORY) list.remove(0);
    }

    public void sendHistory(ClientHandler handler, String room) {
        CopyOnWriteArrayList<Message> list = history.get(room);
        if (list == null || list.isEmpty()) return;
        handler.send(MessageSerializer.serialize(
            new Message(MessageType.SERVER_INFO, null,
                "--- Historique du salon " + room + " (" + list.size() + " messages) ---",
                System.currentTimeMillis())));
        for (Message msg : list) {
            handler.send(MessageSerializer.serialize(msg));
        }
        handler.send(MessageSerializer.serialize(
            new Message(MessageType.SERVER_INFO, null,
                "--- Fin de l'historique ---", System.currentTimeMillis())));
    }

    public void sendUserList(ClientHandler handler, String room) {
        CopyOnWriteArrayList<ClientHandler> list = rooms.get(room);
        if (list == null) return;
        String userList = list.stream()
            .map(c -> c.getPseudo() + ":" + c.getStatus())
            .collect(Collectors.joining(","));
        handler.send(MessageSerializer.serialize(
            new Message(MessageType.USER_LIST, null, userList + "|" + clients.size(),
                System.currentTimeMillis())));
    }

    public void broadcastUserList(String room) {
        CopyOnWriteArrayList<ClientHandler> list = rooms.get(room);
        if (list == null) return;
        String userList = list.stream()
            .map(c -> c.getPseudo() + ":" + c.getStatus())
            .collect(Collectors.joining(","));
        String serialized = MessageSerializer.serialize(
            new Message(MessageType.USER_LIST, null, userList + "|" + clients.size(),
                System.currentTimeMillis()));
        for (ClientHandler client : list) {
            client.send(serialized);
        }
    }

    public void broadcastRoomList() {
        String roomList = rooms.entrySet().stream()
            .map(e -> e.getKey() + ":" + e.getValue().size() + ":" + (roomVisibility.getOrDefault(e.getKey(), true) ? "public" : "private"))
            .collect(Collectors.joining(","));
        String serialized = MessageSerializer.serialize(
            new Message(MessageType.ROOM_LIST, null, roomList, System.currentTimeMillis()));
        broadcastToAll(serialized);
    }

    public ClientHandler getClient(String pseudo) { return clients.get(pseudo); }
    public int getClientCount() { return clients.size(); }

    private void broadcastToAll(String serialized) {
        for (ClientHandler client : clients.values()) {
            client.send(serialized);
        }
    }
}
