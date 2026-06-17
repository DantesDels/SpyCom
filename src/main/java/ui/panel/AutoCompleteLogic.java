package ui.panel;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Binds key events and text changes on the input field to
 * show/hide/navigate the autocomplete popup for slash commands.
 */
public class AutoCompleteLogic {

    private final TextArea inputField;
    private final AutoCompletePopup popup;
    private List<String> commands;
    private int index = -1;

    public AutoCompleteLogic(TextArea inputField, AutoCompletePopup popup) {
        this.inputField = inputField;
        this.popup = popup;
        this.commands = List.of();
        bind();
    }

    public void setCommands(List<String> cmds) { this.commands = cmds; }

    public void close() {
        popup.close();
        index = -1;
    }

    private void bind() {
        inputField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            String t = inputField.getText();
            boolean cmd = t.startsWith("/") && !t.contains(" ");

            if (e.getCode() == KeyCode.TAB && cmd && popup.isVisible()) {
                String sel = popup.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    inputField.setText(sel.split(" ")[0] + " ");
                    inputField.positionCaret(inputField.getText().length());
                }
                close(); e.consume(); return;
            }

            if (e.getCode() == KeyCode.ENTER && !e.isShiftDown() && cmd && popup.isVisible()) {
                String sel = popup.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    inputField.setText(sel.split(" ")[0] + " ");
                    inputField.positionCaret(inputField.getText().length());
                }
                close(); e.consume(); return;
            }

            if (e.getCode() == KeyCode.UP && popup.isVisible()) {
                index = Math.max(0, index - 1);
                popup.getSelectionModel().select(index);
                popup.scrollTo(index);
                e.consume(); return;
            }

            if (e.getCode() == KeyCode.DOWN && popup.isVisible()) {
                index = Math.min(popup.getItems().size() - 1, index + 1);
                popup.getSelectionModel().select(index);
                popup.scrollTo(index);
                e.consume(); return;
            }

            if (e.getCode() == KeyCode.ESCAPE) { close(); e.consume(); }
        });

        inputField.textProperty().addListener((o, ov, nv) -> {
            if (nv == null || !nv.startsWith("/") || nv.contains(" ") || nv.contains("\n")) { close(); return; }
            List<String> f = commands.stream().filter(c -> c.startsWith(nv)).collect(Collectors.toList());
            if (f.isEmpty()) { close(); return; }
            popup.getItems().setAll(f);
            popup.setPrefHeight(Math.min(f.size() * 24 + 10, 150));
            popup.setVisible(true);
            popup.setManaged(true);
            index = -1;
            popup.getSelectionModel().clearSelection();
        });

        inputField.focusedProperty().addListener((o, ov, nv) -> { if (!nv) close(); });
    }
}
