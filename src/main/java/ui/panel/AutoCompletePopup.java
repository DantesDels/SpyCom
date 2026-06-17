package ui.panel;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import static ui.Theme.*;

public class AutoCompletePopup extends ListView<String> {

    public AutoCompletePopup() {
        setStyle(surface(BG_INPUT, BORDER, 3) + fg(ACCENT, FONT_STACK, SZ_SMALL));
        setPrefHeight(0);
        setMaxHeight(150);
        setVisible(false);
        setManaged(false);
        setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(bg(BG_INPUT));
                } else {
                    setText(item);
                    if (isSelected()) {
                        setStyle(bg(ACCENT_DIM) + fg("#000", FONT_STACK, SZ_SMALL) + "-fx-font-weight: bold;");
                    } else {
                        setStyle(bg(BG_INPUT) + fg(ACCENT, FONT_STACK, SZ_SMALL));
                    }
                }
            }
        });
    }

    public void close() {
        setVisible(false);
        setManaged(false);
        setPrefHeight(0);
        getSelectionModel().clearSelection();
    }
}
