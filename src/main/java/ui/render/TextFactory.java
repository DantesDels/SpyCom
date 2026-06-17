package ui.render;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static ui.Theme.*;

/**
 * Shared Text-node factories used by MarkdownRenderer and InlineParser.
 * Allows customising the visual style of every generated Text node.
 */
public final class TextFactory {

    public static Text mkText(String content, String color, String family, double size) {
        Text t = new Text(content);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, size));
        return t;
    }

    public static Text mkText(String content, String color, String family, FontWeight w, double size) {
        Text t = new Text(content);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, w, size));
        return t;
    }

    public static Text mkText(String content, String color, String family, FontPosture p, double size) {
        Text t = new Text(content);
        t.setFill(Color.web(color));
        t.setFont(Font.font(family, p, size));
        return t;
    }

    /** Invisible newline — used to separate block-level lines inside a TextFlow. */
    public static Text newline() {
        Text t = new Text("\n");
        t.setFont(Font.font(FONT, 1));
        return t;
    }

    private TextFactory() {}
}
