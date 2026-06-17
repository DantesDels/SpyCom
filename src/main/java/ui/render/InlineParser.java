package ui.render;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static ui.Theme.*;

/**
 * Scans a single line of text for inline markdown constructs:
 *   **bold**, *italic*, `code`, ~~strikethrough~~, plain text.
 *
 * The regex alternation order is deliberate:
 * longer tokens (~~, **) are tried first so they aren't
 * consumed by shorter single-character matches.
 */
public final class InlineParser {

    private static final Pattern TOKENS = Pattern.compile(
        "(~~([^~]+)~~|\\*\\*([^*]+)\\*\\*|\\*([^*]+)\\*|`([^`]+)`|([^~*`]+))"
    );

    public static List<Text> parseInline(String text) {
        List<Text> result = new ArrayList<>();
        Matcher m = TOKENS.matcher(text);

        while (m.find()) {
            if (m.group(2) != null) {
                Text t = TextFactory.mkText(m.group(2), TEXT, FONT, SZ_NORMAL);
                t.setStrikethrough(true);
                result.add(t);
            } else if (m.group(3) != null) {
                result.add(TextFactory.mkText(m.group(3), TEXT, FONT, FontWeight.BOLD, SZ_NORMAL));
            } else if (m.group(4) != null) {
                result.add(TextFactory.mkText(m.group(4), TEXT, FONT, FontPosture.ITALIC, SZ_NORMAL));
            } else if (m.group(5) != null) {
                result.add(TextFactory.mkText(m.group(5), ACCENT, FONT, FontWeight.BOLD, SZ_SMALL));
            } else if (m.group(6) != null) {
                result.add(TextFactory.mkText(m.group(6), TEXT, FONT, SZ_NORMAL));
            }
        }

        if (result.isEmpty()) {
            result.add(TextFactory.mkText(text, TEXT, FONT, SZ_NORMAL));
        }
        return result;
    }

    private InlineParser() {}
}
