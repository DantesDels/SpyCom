package ui;

import java.io.File;

import client.ChatClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import protocol.Message;
import protocol.MessageType;
import ui.cmd.CommandHandler;
import ui.net.FileSaver;

/**
 * Wires all UI input events (send, status, file upload, drag-and-drop)
 * to their handler counterparts.
 */
public class InputBinder {

    private final ChatClient client;
    private final ChatView view;
    private final ObservableList<Message> messages;
    private final CommandHandler commands;

    public InputBinder(ChatClient client, ChatView view, ObservableList<Message> messages, CommandHandler commands) {
        this.client = client;
        this.view = view;
        this.messages = messages;
        this.commands = commands;
        bindSend();
        bindStatus();
        bindFile();
        bindDrag();
    }

    private void bindSend() {
        Runnable act = () -> {
            String t = view.getInputField().getText();
            if (t == null || t.isBlank()) return;
            view.closeAuto();
            view.getInputField().clear();
            view.getInputField().requestFocus();

            if (t.startsWith("/")) {
                CommandHandler.Result r = commands.execute(t);
                if (r.action() != null) {
                    Platform.runLater(() -> {
                        switch (r.action()) {
                            case "room"   -> view.setCurrentRoom(r.value());
                            case "status" -> view.setCurrentStatus(r.value());
                        }
                    });
                }
                return;
            }

            client.sendText(t);
            messages.add(new Message(MessageType.TEXT, client.getPseudo(), t, System.currentTimeMillis()));
        };
        view.getSendButton().setOnAction(e -> act.run());
        view.getInputField().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (e.isShiftDown()) {
                    int caret = view.getInputField().getCaretPosition();
                    view.getInputField().insertText(caret, "\n");
                } else {
                    act.run();
                }
                e.consume();
            }
        });
    }

    private void bindStatus() {
        view.getStatusCombo().setOnAction(e -> {
            String s = view.getStatusCombo().getValue();
            if (s != null) { client.changeStatus(s); view.setCurrentStatus(s); }
        });
        view.getCustomStatusField().setOnAction(e -> {
            String s = view.getCustomStatusField().getText();
            if (s != null && !s.isBlank()) { client.changeStatus(s); view.setCurrentStatus(s); view.getCustomStatusField().clear(); }
        });
    }

    private void bindFile() {
        view.getFileButton().setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Envoyer un fichier");
            File f = fc.showOpenDialog(view.getScene().getWindow());
            if (f != null && f.exists()) {
                messages.add(new Message(MessageType.SERVER_INFO, null, "Envoi de " + f.getName() + "...", System.currentTimeMillis()));
                client.sendFile(f.toPath());
                FileSaver.saveLocal(client, f.toPath(), messages::add);
            }
        });
    }

    private void bindDrag() {
        view.getMessageList().setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) e.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            e.consume();
        });
        view.getMessageList().setOnDragDropped(e -> {
            for (File f : e.getDragboard().getFiles()) {
                messages.add(new Message(MessageType.SERVER_INFO, null, "Envoi de " + f.getName() + "...", System.currentTimeMillis()));
                client.sendFile(f.toPath());
                FileSaver.saveLocal(client, f.toPath(), messages::add);
            }
            e.setDropCompleted(true);
            e.consume();
        });
    }
}
