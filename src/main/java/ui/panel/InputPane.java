package ui.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import static ui.Theme.ACCENT;
import static ui.Theme.ACCENT_DIM;
import static ui.Theme.BG_INPUT;
import static ui.Theme.BG_TEXTAREA;
import static ui.Theme.BG_PANEL;
import static ui.Theme.BORDER;
import static ui.Theme.FONT_STACK;
import static ui.Theme.MUTED;
import static ui.Theme.SZ_LARGE;
import static ui.Theme.SZ_MEDIUM;
import static ui.Theme.SZ_NORMAL;
import static ui.Theme.TEXT;
import static ui.Theme.bg;
import static ui.Theme.border;
import static ui.Theme.btn;
import static ui.Theme.fg;
import static ui.Theme.padding;
import static ui.Theme.surface;

public class InputPane extends HBox {
    private final Button fileButton;
    private final TextArea inputField;
    private final Button sendButton;

    public InputPane() {
        fileButton = new Button("\uD83D\uDCCE");
        fileButton.setStyle(surface(BG_INPUT, BORDER, 4) + fg(TEXT, FONT_STACK, SZ_LARGE) + padding(6, 10) + "-fx-cursor: hand;");
        fileButton.setTooltip(new Tooltip("Envoyer un fichier"));

        inputField = new TextArea();
        inputField.setPromptText("Tapez votre message... (/help)");
        inputField.setWrapText(true);
        inputField.setPrefRowCount(1);
        inputField.setMaxHeight(100);
        inputField.setStyle(surface(BG_TEXTAREA, BORDER, 4) + fg(TEXT, FONT_STACK, SZ_MEDIUM)
            + "-fx-prompt-text-fill: " + MUTED + ";"
            + "-fx-control-inner-background: " + BG_TEXTAREA + ";"
            + padding(8, 12));

        sendButton = mkSendButton();

        setSpacing(8);
        setPadding(new Insets(10, 15, 10, 15));
        setAlignment(Pos.CENTER);
        setStyle(bg(BG_PANEL) + border(MUTED + " transparent transparent transparent", "1 0 0 0"));
        HBox.setHgrow(inputField, Priority.ALWAYS);
        getChildren().addAll(fileButton, inputField, sendButton);
    }

    private static Button mkSendButton() {
        Button b = new Button("ENVOYER");
        String[] s = btn(ACCENT_DIM, ACCENT, "#000", FONT_STACK, SZ_NORMAL);
        b.setStyle(s[0]);
        b.setOnMouseEntered(e -> b.setStyle(s[1]));
        b.setOnMouseExited(e -> b.setStyle(s[0]));
        return b;
    }

    public Button getFileButton() { return fileButton; }
    public TextArea getInputField() { return inputField; }
    public Button getSendButton() { return sendButton; }
}