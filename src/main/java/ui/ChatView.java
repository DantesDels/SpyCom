package ui;

import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import protocol.Message;
import ui.panel.AutoCompleteLogic;
import ui.panel.AutoCompletePopup;
import ui.panel.HeaderBar;
import ui.panel.InputPane;
import ui.panel.MessageBoard;
import ui.panel.SidebarPane;
import static ui.Theme.*;

public class ChatView {
    private final HeaderBar header;
    private final MessageBoard messageBoard;
    private final SidebarPane sidebar;
    private final InputPane input;
    private final AutoCompletePopup autoComplete;
    private final AutoCompleteLogic autoLogic;
    private final Scene scene;
    private final TextArea inputField;
    private Stage stage;

    public ChatView(String pseudo, String host, int port) {
        header = new HeaderBar(pseudo, host, port);
        messageBoard = new MessageBoard();
        sidebar = new SidebarPane();
        input = new InputPane();
        autoComplete = new AutoCompletePopup();
        inputField = input.getInputField();

        sidebar.getRoomList().setOnMouseClicked(e -> {
            String sel = sidebar.getRoomList().getSelectionModel().getSelectedItem();
            if (sel != null) {
                String room = sel.replaceAll("[\\uD83D\\uDD10\\uD83D\\uDD12]", "").replaceAll("\\(.*\\)", "").trim();
                inputField.setText("/join " + room);
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(messageBoard);
        root.setRight(sidebar);
        root.setBottom(new VBox(autoComplete, input));
        root.setStyle(bg(BG_DARK));

        scene = new Scene(root, 950, 600);
        scene.widthProperty().addListener((o, ov, nv) ->
            sidebar.setPrefWidth(Math.max(180, nv.doubleValue() * 0.22)));

        autoLogic = new AutoCompleteLogic(inputField, autoComplete);
    }

    public void setCommands(List<String> cmds) { autoLogic.setCommands(cmds); }

    // ── Public API ─────────────────────────────────────────
    public void setStage(Stage s) { this.stage = s; }
    public void updateTitle(String p) { if (stage != null) stage.setTitle("Chat Secure - Agent: " + p); }
    public void closeAuto() { autoLogic.close(); }

    public void updatePseudo(String p)     { header.updatePseudo(p); }
    public void setCurrentStatus(String s) { header.setCurrentStatus(s); }
    public void setConnected(boolean c)    { header.setConnected(c); }
    public void setCurrentRoom(String r)   { sidebar.setCurrentRoom(r); }
    public void setTotalAgents(int n)      { sidebar.setTotalAgents(n); }

    public TextArea getInputField()        { return input.getInputField(); }
    public Button getSendButton()           { return input.getSendButton(); }
    public Button getFileButton()           { return input.getFileButton(); }
    public ListView<Message> getMessageList()   { return messageBoard.getMessageList(); }
    public ListView<String> getUserList()        { return sidebar.getUserList(); }
    public ListView<String> getRoomList()        { return sidebar.getRoomList(); }
    public Label getUserCountLabel()             { return sidebar.getUserCountLabel(); }
    public ComboBox<String> getStatusCombo()     { return header.getStatusCombo(); }
    public TextField getCustomStatusField()      { return header.getCustomStatusField(); }
    public Scene getScene()                      { return scene; }
}
