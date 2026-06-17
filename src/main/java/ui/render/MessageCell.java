
package ui.render;

import java.io.FileInputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import protocol.Message;
import protocol.MessageType;
import static ui.Theme.*;

public class MessageCell extends ListCell<Message> {
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    private final TextFlow flow = new TextFlow();
    private final Text timeText = new Text();
    private final Text pseudoText = new Text();
    private final Label systemLabel = new Label();
    private final Label privateLabel = new Label();
    private final VBox fileBox = new VBox(4);
    private final Label fileNameLabel = new Label();
    private final ImageView imageView = new ImageView();
    private final TextArea textPreview = new TextArea();

    public MessageCell() {
        flow.setLineSpacing(2);
        setStyle(bg("transparent") + padding(4, 12, 4, 12));

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(280);
        imageView.setStyle(border(BORDER, "1", "4px"));

        textPreview.setEditable(false);
        textPreview.setWrapText(true);
        textPreview.setPrefHeight(120);
        textPreview.setMaxWidth(Double.MAX_VALUE);
        textPreview.setStyle(surface(BG_INPUT, BORDER, 4) + font(FONT_STACK, SZ_SMALL) +
            "-fx-control-inner-background: " + BG_INPUT + ";");

        fileNameLabel.setStyle(fg(ACCENT, FONT_STACK, SZ_SMALL));
        fileBox.setPadding(new Insets(4, 0, 4, 0));
    }

    @Override
    protected void updateItem(Message msg, boolean empty) {
        super.updateItem(msg, empty);
        if (empty || msg == null) { setGraphic(null); return; }

        if (msg.getType() == MessageType.FILE_COMPLETE_IMG) {
            fileBox.getChildren().clear();
            fileNameLabel.setText("\uD83D\uDDBC  " + (msg.getExtra() != null ? msg.getExtra() : "Image"));
            try {
                imageView.setImage(new Image(new FileInputStream(msg.getContenu()), 280, 0, true, false));
            } catch (Exception e) { imageView.setImage(null); }
            Button openDirBtn = mkFileBtn("\uD83D\uDCC2 Dossier", e -> {
                try { java.awt.Desktop.getDesktop().open(new java.io.File(msg.getContenu()).getParentFile()); }
                catch (Exception ex) {}
            });
            Button openFileBtn = mkFileBtn("\uD83D\uDCC1 Ouvrir", e -> {
                try { java.awt.Desktop.getDesktop().open(new java.io.File(msg.getContenu())); }
                catch (Exception ex) {}
            });
            fileBox.getChildren().addAll(fileNameLabel, imageView, new HBox(8, openFileBtn, openDirBtn));
            setGraphic(fileBox); return;
        }

        if (msg.getType() == MessageType.FILE_COMPLETE_TXT) {
            fileBox.getChildren().clear();
            String extra = msg.getExtra();
            String[] parts = extra != null ? extra.split("\\|", 2) : new String[]{msg.getContenu(), "fichier"};
            fileNameLabel.setText("\uD83D\uDCC4  " + (parts.length > 1 ? parts[1] : "fichier"));
            textPreview.setText(msg.getContenu());
            String filePath = parts.length > 1 ? parts[0] : "";
            Button openDirBtn = mkFileBtn("\uD83D\uDCC2 Dossier", e -> {
                try { java.awt.Desktop.getDesktop().open(new java.io.File(filePath).getParentFile()); }
                catch (Exception ex) {}
            });
            Button openFileBtn = mkFileBtn("\uD83D\uDCC1 Ouvrir", e -> {
                try { java.awt.Desktop.getDesktop().open(new java.io.File(filePath)); }
                catch (Exception ex) {}
            });
            fileBox.getChildren().addAll(fileNameLabel, textPreview, new HBox(8, openFileBtn, openDirBtn));
            setGraphic(fileBox); return;
        }

        if (msg.getType() == MessageType.FILE_COMPLETE_OTHER) {
            fileBox.getChildren().clear();
            String fname = msg.getExtra() != null ? msg.getExtra() : "fichier";
            fileNameLabel.setText("\uD83D\uDCCE  " + fname);
            Button openBtn = mkFileBtn("\uD83D\uDCC2 Dossier", e -> {
                try { java.awt.Desktop.getDesktop().open(new java.io.File(msg.getContenu()).getParentFile()); }
                catch (Exception ex) {}
            });
            Button openFileBtn = mkFileBtn("\uD83D\uDCC1 Ouvrir", e -> {
                try { java.awt.Desktop.getDesktop().open(new java.io.File(msg.getContenu())); }
                catch (Exception ex) {}
            });
            fileBox.getChildren().addAll(fileNameLabel, new HBox(8, openFileBtn, openBtn));
            setGraphic(fileBox); return;
        }

        if (msg.getType() == MessageType.FILE_META) {
            fileLabel(msg);
            return;
        }

        if (msg.getType() == MessageType.PRIVATE_MSG) {
            privateLabel.setText("\uD83D\uDD12 " + (msg.getPseudo() != null ? msg.getPseudo() + " \u203A " : "") + msg.getContenu());
            privateLabel.setStyle(fg(PRIVATE, FONT_STACK, SZ_SMALL));
            privateLabel.setMaxWidth(Double.MAX_VALUE);
            setGraphic(privateLabel); return;
        }

        if (msg.getType() == MessageType.SERVER_INFO) {
            systemLabel.setText(msg.getContenu());
            systemLabel.setStyle(fg(MUTED, FONT_STACK, SZ_SMALL));
            systemLabel.setAlignment(Pos.CENTER_LEFT);
            systemLabel.setMaxWidth(Double.MAX_VALUE);
            setGraphic(systemLabel); return;
        }

        flow.getChildren().clear();
        flow.setMaxWidth(Double.MAX_VALUE);
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
            List<Text> mdNodes = renderMarkdown(msg.getContenu());
            flow.getChildren().addAll(mdNodes);
        }
        setGraphic(flow);
    }

    private void fileLabel(Message msg) {
        Label l = new Label("\uD83D\uDCCE Fichier: " + (msg.getContenu() != null ? msg.getContenu() : "?"));
        l.setStyle(fg(ACCENT, FONT_STACK, SZ_SMALL));
        l.setMaxWidth(Double.MAX_VALUE);
        setGraphic(l);
    }

    private static Button mkFileBtn(String label, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button b = new Button(label);
        b.setStyle(bg(ACCENT_DIM) + fg("#000", FONT_STACK, SZ_TINY) +
            "-fx-background-radius: 3px; -fx-padding: 4 10; -fx-cursor: hand;");
        b.setOnAction(handler);
        return b;
    }

    // ── Markdown ────────────────────────────────────────────
    private static List<Text> renderMarkdown(String text) {
        List<Text> result = new ArrayList<>();
        StringBuilder codeBuf = new StringBuilder();
        boolean inCodeBlock = false;

        for (String line : text.split("\n", -1)) {
            if (line.trim().startsWith("```")) {
                if (inCodeBlock) {
                    result.add(blockText(codeBuf.toString(), MUTED, FONT, SZ_SMALL));
                    codeBuf.setLength(0);
                    inCodeBlock = false;
                } else {
                    inCodeBlock = true;
                }
                continue;
            }
            if (inCodeBlock) {
                if (codeBuf.length() > 0) codeBuf.append('\n');
                codeBuf.append(line);
                continue;
            }

            String trimmed = line;
            if (trimmed.startsWith("### ")) {
                result.add(blockText(trimmed.substring(4), ACCENT, FONT, FontWeight.BOLD, 13));
                continue;
            }
            if (trimmed.startsWith("## ")) {
                result.add(blockText(trimmed.substring(3), ACCENT, FONT, FontWeight.BOLD, 14));
                continue;
            }
            if (trimmed.startsWith("# ")) {
                result.add(blockText(trimmed.substring(2), ACCENT, FONT, FontWeight.BOLD, 15));
                continue;
            }

            if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                Text bullet = mdText("\u2022 ", ACCENT, FONT, FontWeight.BOLD, SZ_NORMAL);
                result.add(bullet);
                result.addAll(parseInline(trimmed.substring(2)));
                result.add(newLine());
                continue;
            }
            if (trimmed.matches("^\\d+\\.\\s.*")) {
                String num = trimmed.replaceAll("^(\\d+\\.\\s).*$", "$1");
                Text bullet = mdText(num, ACCENT, FONT, FontWeight.BOLD, SZ_NORMAL);
                result.add(bullet);
                result.addAll(parseInline(trimmed.substring(num.length())));
                result.add(newLine());
                continue;
            }
            if (trimmed.startsWith("> ")) {
                Text quote = mdText("\u203A ", MUTED, FONT, FontPosture.ITALIC, SZ_SMALL);
                result.add(quote);
                result.addAll(parseInline(trimmed.substring(2)));
                result.add(newLine());
                continue;
            }

            result.addAll(parseInline(trimmed));
            result.add(newLine());
        }

        if (inCodeBlock && codeBuf.length() > 0) {
            result.add(blockText(codeBuf.toString(), MUTED, FONT, SZ_SMALL));
        }

        return result;
    }

    private static List<Text> parseInline(String text) {
        List<Text> result = new ArrayList<>();
        Pattern p = Pattern.compile(
            "(~~([^~]+)~~|\\*\\*([^*]+)\\*\\*|\\*([^*]+)\\*|`([^`]+)`|([^~*`]+))");
        Matcher m = p.matcher(text);

        while (m.find()) {
            if (m.group(2) != null) {
                Text t = mdText(m.group(2), TEXT, FONT, SZ_NORMAL);
                t.setStrikethrough(true);
                result.add(t);
            } else if (m.group(3) != null) {
                result.add(mdText(m.group(3), TEXT, FONT, FontWeight.BOLD, SZ_NORMAL));
            } else if (m.group(4) != null) {
                result.add(mdText(m.group(4), TEXT, FONT, FontPosture.ITALIC, SZ_NORMAL));
            } else if (m.group(5) != null) {
                result.add(mdText(m.group(5), ACCENT, FONT, FontWeight.BOLD, SZ_SMALL));
            } else if (m.group(6) != null) {
                result.add(mdText(m.group(6), TEXT, FONT, SZ_NORMAL));
            }
        }

        if (result.isEmpty()) {
            result.add(mdText(text, TEXT, FONT, SZ_NORMAL));
        }
        return result;
    }

    private static Text blockText(String content, String color, String family, double size) {
        Text t = new Text(content);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, size));
        return t;
    }

    private static Text blockText(String content, String color, String family, FontWeight w, double size) {
        Text t = new Text(content);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, w, size));
        return t;
    }

    private static Text mdText(String text, String color, String family, double size) {
        Text t = new Text(text);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, size));
        return t;
    }

    private static Text mdText(String text, String color, String family, FontWeight w, double size) {
        Text t = new Text(text);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, w, size));
        return t;
    }

    private static Text mdText(String text, String color, String family, FontPosture p, double size) {
        Text t = new Text(text);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, p, size));
        return t;
    }

    private static Text newLine() {
        Text t = new Text("\n");
        t.setFont(Font.font(FONT, 1));
        return t;
    }
}