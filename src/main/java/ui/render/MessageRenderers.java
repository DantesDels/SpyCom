package ui.render;

import java.io.FileInputStream;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import protocol.Message;
import static ui.Theme.*;

public final class MessageRenderers {

    public static void renderImage(ListCell<Message> cell, FileDisplayNodes n, Message msg) {
        n.fileBox.getChildren().clear();
        n.fileNameLabel.setText("\uD83D\uDDBC  " + (msg.getExtra() != null ? msg.getExtra() : "Image"));
        try (FileInputStream fis = new FileInputStream(msg.getContenu())) {
            n.imageView.setImage(new Image(fis, 280, 0, true, false));
        } catch (Exception e) {
            n.imageView.setImage(null);
        }
        n.fileBox.getChildren().addAll(n.fileNameLabel, n.imageView);
        cell.setGraphic(n.fileBox);
    }

    public static void renderText(ListCell<Message> cell, FileDisplayNodes n, Message msg) {
        n.fileBox.getChildren().clear();
        String extra = msg.getExtra();
        String[] parts = extra != null ? extra.split("\\|", 2) : new String[]{msg.getContenu(), "fichier"};
        n.fileNameLabel.setText("\uD83D\uDCC4  " + (parts.length > 1 ? parts[1] : "fichier"));
        n.textPrev.setText(msg.getContenu());
        n.fileBox.getChildren().addAll(n.fileNameLabel, n.textPrev);
        cell.setGraphic(n.fileBox);
    }

    public static void renderFile(ListCell<Message> cell, FileDisplayNodes n, Message msg) {
        n.fileBox.getChildren().clear();
        String fname = msg.getExtra() != null ? msg.getExtra() : "fichier";
        n.fileNameLabel.setText("\uD83D\uDCCE  " + fname);
        n.setFilePath(msg.getContenu());
        n.fileBox.getChildren().addAll(n.fileNameLabel, n.fileBtnBox);
        cell.setGraphic(n.fileBox);
    }

    public static void renderPrivate(ListCell<Message> cell, Label privateLabel, Message msg) {
        privateLabel.setText("\uD83D\uDD12 " + (msg.getPseudo() != null ? msg.getPseudo() + " \u203A " : "") + msg.getContenu());
        privateLabel.setStyle(fg(PRIVATE, FONT_STACK, SZ_SMALL));
        privateLabel.setMaxWidth(Double.MAX_VALUE);
        cell.setGraphic(privateLabel);
    }

    public static void renderSystem(ListCell<Message> cell, Label systemLabel, Message msg) {
        systemLabel.setText(msg.getContenu());
        systemLabel.setStyle(fg(MUTED, FONT_STACK, SZ_SMALL));
        systemLabel.setAlignment(Pos.CENTER_LEFT);
        systemLabel.setMaxWidth(Double.MAX_VALUE);
        cell.setGraphic(systemLabel);
    }

    private MessageRenderers() {}
}
