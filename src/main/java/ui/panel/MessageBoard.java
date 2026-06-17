package ui.panel;

import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import protocol.Message;
import static ui.Theme.BG_DARK;
import static ui.Theme.bg;
import static ui.Theme.listBg;
import ui.render.MessageCell;

public class MessageBoard extends VBox {
    private final ListView<Message> messageList;

    public MessageBoard() {
        messageList = new ListView<>();
        messageList.setCellFactory(v -> new MessageCell());
        messageList.setStyle(listBg(BG_DARK));

        getChildren().add(messageList);
        setStyle(bg(BG_DARK));
        VBox.setVgrow(messageList, Priority.ALWAYS);
    }

    public ListView<Message> getMessageList() { return messageList; }
}