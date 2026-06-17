package ui.render;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static ui.Theme.*;

public class UserCell extends ListCell<String> {
    private final Circle dot = new Circle(5);
    private final Text pseudoText = new Text();
    private final Text statusText = new Text();

    public UserCell() {
        setStyle(bg("transparent") + padding(3, 10));
        pseudoText.setFont(Font.font(FONT, FontWeight.BOLD, SZ_NORMAL));
        statusText.setFont(Font.font(FONT, SZ_TINY));
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) { setGraphic(null); return; }

        String[] parts = item.split("\\|", 4);
        String pseudo = parts[0];
        String status = parts.length > 1 ? parts[1] : "online";

        dot.setFill(Color.web(statusColor(status)));

        pseudoText.setText(pseudo);
        pseudoText.setFill(Color.web(ACCENT));

        statusText.setText("[" + status + "]");
        statusText.setFill(Color.web(MUTED));

        HBox box = new HBox(6, dot, pseudoText, statusText);
        box.setAlignment(Pos.CENTER_LEFT);
        setGraphic(box);
    }
}
