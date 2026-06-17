package ui.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import static ui.Theme.ACCENT;
import static ui.Theme.AMBER;
import static ui.Theme.BG_INPUT;
import static ui.Theme.BG_PANEL;
import static ui.Theme.BORDER;
import static ui.Theme.DANGER;
import static ui.Theme.FONT_STACK;
import static ui.Theme.MUTED;
import static ui.Theme.SZ_LARGE;
import static ui.Theme.SZ_NORMAL;
import static ui.Theme.SZ_SMALL;
import static ui.Theme.SZ_TINY;
import static ui.Theme.TEXT;
import static ui.Theme.bg;
import static ui.Theme.border;
import static ui.Theme.dotStyle;
import static ui.Theme.fg;
import static ui.Theme.padding;
import static ui.Theme.statusColor;
import static ui.Theme.surface;
import static ui.Theme.text;

public class HeaderBar extends VBox {
    private final Label statusLabel;
    private final Label agentLabel;
    private final Label statusDot;
    private final ComboBox<String> statusCombo;
    private final TextField customStatusField;

    public HeaderBar(String pseudo, String host, int port) {
        Label title = new Label("\uD83D\uDD10  SysCom");
        title.setStyle(fg(ACCENT, FONT_STACK, SZ_LARGE) + "-fx-font-weight: bold;");

        statusLabel = new Label("\u25CF  CONNEXION S\u00C9CURIS\u00C9E");
        statusLabel.setStyle(fg(ACCENT, FONT_STACK, SZ_SMALL));

        agentLabel = new Label(pseudo.toUpperCase());
        agentLabel.setStyle(fg(AMBER, FONT_STACK, SZ_NORMAL));

        statusDot = new Label("\u25CF");
        statusDot.setStyle(text(ACCENT) + "-fx-font-size: 14px;");

        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("online", "busy", "afk");
        statusCombo.setValue("online");
        statusCombo.setStyle(surface(BG_INPUT, BORDER, 3) + fg(TEXT, FONT_STACK, SZ_SMALL) + "-fx-pref-width: 85px;");

        customStatusField = new TextField();
        customStatusField.setPrefWidth(90);
        customStatusField.setPromptText("custom");
        customStatusField.setStyle(surface(BG_INPUT, BORDER, 3) + fg(TEXT, FONT_STACK, SZ_SMALL) + padding(2, 6));

        Label labelStatut = new Label("STATUT:");
        labelStatut.setStyle(fg(MUTED, FONT_STACK, SZ_SMALL));

        HBox statusBox = new HBox(3, statusDot, labelStatut, statusCombo, customStatusField);
        statusBox.setAlignment(Pos.CENTER_RIGHT);

        HBox top = new HBox(15, title, agentLabel, statusLabel, statusBox);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(8, 15, 4, 15));
        top.setStyle(bg(BG_PANEL) + border("transparent transparent " + BORDER + " transparent", "0 0 1 0"));
        HBox.setHgrow(statusBox, Priority.ALWAYS);

        Label commandLabel = new Label("> gradle run -Pargs=\"" + host + " " + port + " Pseudo\"");
        commandLabel.setStyle(fg(MUTED, FONT_STACK, SZ_TINY) + padding(3, 15));
        commandLabel.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(top, commandLabel);
        setStyle(bg(BG_PANEL));
    }

    public void setConnected(boolean c) {
        statusLabel.setText(c ? "\u25CF  CONNEXION S\u00C9CURIS\u00C9E" : "\u25CB  CONNEXION PERDUE");
        statusLabel.setStyle(fg(c ? ACCENT : DANGER, FONT_STACK, SZ_SMALL));
    }

    public void setCurrentStatus(String s) {
        statusCombo.setValue(s);
        statusDot.setStyle(dotStyle(statusColor(s)));
    }

    public void updatePseudo(String p) { agentLabel.setText(p.toUpperCase()); }

    public ComboBox<String> getStatusCombo() { return statusCombo; }
    public TextField getCustomStatusField() { return customStatusField; }
}