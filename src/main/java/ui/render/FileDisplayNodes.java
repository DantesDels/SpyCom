package ui.render;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static ui.Theme.*;

public class FileDisplayNodes {

    public final VBox fileBox;
    public final Label fileNameLabel;
    public final ImageView imageView;
    public final TextArea textPrev;
    public final Button openFileBtn;
    public final Button openDirBtn;
    public final HBox fileBtnBox;
    private String filePath;

    public FileDisplayNodes() {
        fileBox = new VBox(4);
        fileNameLabel = new Label();
        imageView = new ImageView();
        textPrev = new TextArea();

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(280);
        imageView.setStyle(border(BORDER, "1", "4px"));

        textPrev.setEditable(false);
        textPrev.setWrapText(true);
        textPrev.setPrefHeight(120);
        textPrev.setMaxWidth(Double.MAX_VALUE);
        textPrev.setStyle(surface(BG_INPUT, BORDER, 4) + font(FONT_STACK, SZ_SMALL) +
            "-fx-control-inner-background: " + BG_INPUT + ";");

        openFileBtn = mkBtn("\uD83D\uDCC1 Ouvrir");
        openFileBtn.setOnAction(e -> {
            try { java.awt.Desktop.getDesktop().open(new java.io.File(filePath)); }
            catch (Exception ex) {}
        });
        openDirBtn = mkBtn("\uD83D\uDCC2 Dossier");
        openDirBtn.setOnAction(e -> {
            try { java.awt.Desktop.getDesktop().open(new java.io.File(filePath).getParentFile()); }
            catch (Exception ex) {}
        });
        fileBtnBox = new HBox(8, openFileBtn, openDirBtn);

        fileNameLabel.setStyle(fg(ACCENT, FONT_STACK, SZ_SMALL));
        fileBox.setPadding(new Insets(4, 0, 4, 0));
    }

    public void setFilePath(String path) { this.filePath = path; }

    private static Button mkBtn(String label) {
        Button b = new Button(label);
        b.setStyle(bg(ACCENT_DIM) + fg("#000", FONT_STACK, SZ_TINY) +
            "-fx-background-radius: 3px; -fx-padding: 4 10; -fx-cursor: hand;");
        return b;
    }
}
