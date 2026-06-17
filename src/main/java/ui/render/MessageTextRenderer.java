package ui.render;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import protocol.Message;
import static ui.Theme.*;

/**
 * Renders a default chat message (timestamp + optional pseudo + markdown body).
 */
public class MessageTextRenderer {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    public void render(ListCell<Message> cell, Message msg) {
        TextFlow flow = new TextFlow();
        flow.setLineSpacing(2);
        flow.setMaxWidth(Double.MAX_VALUE);
        Text timeText = new Text();
        Text pseudoText = new Text();

        String ts = FMT.format(Instant.ofEpochMilli(msg.getTimestamp()));
        timeText.setText(ts + "  ");
        timeText.setFill(Color.web(MUTED));
        timeText.setFont(Font.font(FONT, SZ_SMALL));

        if (msg.getPseudo() != null) {
            pseudoText.setText(msg.getPseudo() + " \u203A ");
            pseudoText.setFill(Color.web(ACCENT));
            pseudoText.setFont(Font.font(FONT, FontWeight.BOLD, SZ_NORMAL));
            flow.getChildren().addAll(timeText, pseudoText);
        } else {
            flow.getChildren().add(timeText);
        }

        if (msg.getContenu() != null) {
            flow.getChildren().addAll(MarkdownRenderer.render(msg.getContenu()));
        }
        cell.setGraphic(flow);
    }
}
