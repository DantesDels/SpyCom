package ui;

import java.io.IOException;
import java.util.List;

import client.ChatClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import protocol.Message;
import protocol.MessageType;
import ui.cmd.CommandHandler;
import ui.net.FileReceiver;
import ui.net.UserRoomManager;

public class ChatController {
    private final ChatClient client;
    private final ChatView view;

    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final ObservableList<String> users   = FXCollections.observableArrayList();
    private final ObservableList<String> rooms   = FXCollections.observableArrayList();

    private final CommandHandler commands;
    private final InputBinder inputBinder;
    private final FileReceiver fileReceiver;
    private final UserRoomManager userRoom;

    public ChatController(ChatView v, ChatClient c) {
        this.client = c;
        this.view = v;

        view.getMessageList().setItems(messages);
        view.getUserList().setItems(users);
        view.getRoomList().setItems(rooms);

        commands = new CommandHandler(client, messages::add);
        fileReceiver = new FileReceiver(messages::add);
        userRoom = new UserRoomManager(users, rooms,
                    count -> Platform.runLater(() -> view.getUserCountLabel().setText(count)),
                    n -> Platform.runLater(() -> view.setTotalAgents(n)));

        inputBinder = new InputBinder(client, v, messages, commands);

        client.setCallback(this::onMsg);
    }

    public void connect() {
        try { client.connect(); Platform.runLater(() -> view.setConnected(true)); }
        catch (IOException e) {
            Platform.runLater(() -> messages.add(
                new Message(MessageType.SERVER_INFO, null, "Erreur: " + e.getMessage(), System.currentTimeMillis())));
        }
    }

    public void disconnect() { client.disconnect(); Platform.runLater(() -> view.setConnected(false)); }
    public List<String> getCommands() { return commands.getCommands(); }

    private void onMsg(Message m) {
        Platform.runLater(() -> {
            switch (m.getType()) {
                case TEXT, SERVER_INFO, HISTORY,
                     FILE_COMPLETE_IMG, FILE_COMPLETE_TXT, FILE_COMPLETE_OTHER,
                     PRIVATE_MSG -> messages.add(m);

                case USER_LIST -> userRoom.updateUsers(m.getContenu());
                case ROOM_LIST -> userRoom.updateRooms(m.getContenu());

                case FILE_META -> fileReceiver.onMeta(m);
                case FILE_DATA  -> fileReceiver.onData(m);

                case NICK_CHANGE -> {
                    if (m.getContenu() != null) {
                        client.setPseudo(m.getContenu());
                        view.updatePseudo(m.getContenu());
                        view.updateTitle(m.getContenu());
                    }
                }

                case PSEUDO_TAKEN -> { messages.add(m); view.setConnected(false); }
                default -> {}
            }
        });
    }
}
