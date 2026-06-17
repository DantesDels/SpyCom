package ui.panel;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ui.Theme.ACCENT;
import static ui.Theme.AMBER;
import static ui.Theme.BG_PANEL;
import static ui.Theme.BORDER;
import static ui.Theme.FONT_STACK;
import static ui.Theme.MUTED;
import static ui.Theme.SZ_SMALL;
import static ui.Theme.SZ_TINY;
import static ui.Theme.bg;
import static ui.Theme.border;
import static ui.Theme.fg;
import static ui.Theme.listBg;
import static ui.Theme.padding;
import ui.render.UserCell;

public class SidebarPane extends VBox {
    private final Label totalAgentsLabel;
    private final Label roomLabel;
    private final Label userCountLabel;
    private final ListView<String> userList;
    private final ListView<String> roomList;
    private String currentRoom = "general";

    public SidebarPane() {
        Label agentsH = sectionHeader("AGENTS CONNECT\u00C9S");
        Label roomH = sectionHeader("SALONS");

        totalAgentsLabel = new Label("Total: 0 agents");
        totalAgentsLabel.setStyle(fg(MUTED, FONT_STACK, SZ_TINY) + padding(0, 10, 5, 10));

        roomLabel = new Label("SALON: general");
        roomLabel.setStyle(fg(AMBER, FONT_STACK, SZ_SMALL) + "-fx-font-weight: bold;" + padding(0, 10, 5, 10));

        Label sep = new Label("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        sep.setStyle(fg(MUTED, FONT_STACK, SZ_TINY) + padding(0, 10, 5, 10));

        userCountLabel = new Label("0 en ligne");
        userCountLabel.setStyle(fg(MUTED, FONT_STACK, SZ_TINY) + padding(0, 10, 5, 10));

        userList = new ListView<>();
        userList.setCellFactory(v -> new UserCell());
        userList.setStyle(listBg(BG_PANEL));
        userList.setMaxHeight(200);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        roomList = new ListView<>();
        roomList.setStyle(listBg(BG_PANEL) + fg(AMBER, FONT_STACK, SZ_SMALL));
        roomList.setPrefHeight(100);
        roomList.setPlaceholder(new Label("Aucun salon"));
        roomList.getPlaceholder().setStyle(fg(MUTED, FONT_STACK, SZ_SMALL));
        roomList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(bg(BG_PANEL));
                } else {
                    setText(item);
                    String roomName = item.replaceAll("[\\uD83D\\uDD10\\uD83D\\uDD12\\u27A4]", "").replaceAll("\\(.*\\)", "").trim();
                    if (roomName.equals(currentRoom)) {
                        setStyle(bg(BG_PANEL) + fg(ACCENT, FONT_STACK, SZ_SMALL) + "-fx-font-weight: bold;");
                        setText("\u27A4 " + item);
                    } else {
                        setStyle(bg(BG_PANEL) + fg(AMBER, FONT_STACK, SZ_SMALL));
                    }
                }
            }
        });

        getChildren().addAll(agentsH, totalAgentsLabel, roomLabel, sep, userCountLabel, userList, spacer, roomH, roomList);
        setStyle(bg(BG_PANEL) + border("transparent transparent transparent " + BORDER, "0 0 0 1"));
        VBox.setVgrow(roomList, Priority.ALWAYS);
        setPrefWidth(220);
    }

    private static Label sectionHeader(String text) {
        Label l = new Label(text);
        l.setStyle(fg(ACCENT, FONT_STACK, SZ_SMALL) + "-fx-font-weight: bold;" + padding(10, 10, 5, 10));
        return l;
    }

    public void setTotalAgents(int n) { totalAgentsLabel.setText("Total: " + n + " agents"); }
    public void setCurrentRoom(String r) {
        currentRoom = r;
        roomLabel.setText("SALON: " + r);
        roomList.refresh();
    }

    public ListView<String> getUserList() { return userList; }
    public ListView<String> getRoomList() { return roomList; }
    public Label getUserCountLabel() { return userCountLabel; }
}