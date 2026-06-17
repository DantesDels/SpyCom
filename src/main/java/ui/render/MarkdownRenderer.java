package ui.render;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static ui.Theme.*;

/**
 * Minimal markdown block-level renderer.
 *
 * Splits text by newline and processes:
 *   # ## ### headers, - /* lists, 1. numbered, > blockquote, ```code blocks```
 * Inline formatting (**bold**, *italic*, `code`, ~~strikethrough~~) is delegated
 * to {@link InlineParser}.
 */
public final class MarkdownRenderer {

    public static List<Text> render(String text) {
        List<Text> result = new ArrayList<>();
        StringBuilder codeBuf = new StringBuilder();
        boolean inCodeBlock = false;

        for (String line : text.split("\n", -1)) {
            if (line.trim().startsWith("```")) {
                if (inCodeBlock) {
                    result.add(TextFactory.mkText(codeBuf.toString(), MUTED, FONT, SZ_SMALL));
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
                result.add(TextFactory.mkText(trimmed.substring(4), ACCENT, FONT, FontWeight.BOLD, 13));
                continue;
            }
            if (trimmed.startsWith("## ")) {
                result.add(TextFactory.mkText(trimmed.substring(3), ACCENT, FONT, FontWeight.BOLD, 14));
                continue;
            }
            if (trimmed.startsWith("# ")) {
                result.add(TextFactory.mkText(trimmed.substring(2), ACCENT, FONT, FontWeight.BOLD, 15));
                continue;
            }

            if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                result.add(TextFactory.mkText("\u2022 ", ACCENT, FONT, FontWeight.BOLD, SZ_NORMAL));
                result.addAll(InlineParser.parseInline(trimmed.substring(2)));
                result.add(TextFactory.newline());
                continue;
            }
            if (trimmed.matches("^\\d+\\.\\s.*")) {
                String num = trimmed.replaceAll("^(\\d+\\.\\s).*$", "$1");
                result.add(TextFactory.mkText(num, ACCENT, FONT, FontWeight.BOLD, SZ_NORMAL));
                result.addAll(InlineParser.parseInline(trimmed.substring(num.length())));
                result.add(TextFactory.newline());
                continue;
            }
            if (trimmed.startsWith("> ")) {
                result.add(TextFactory.mkText("\u203A ", MUTED, FONT, FontPosture.ITALIC, SZ_SMALL));
                result.addAll(InlineParser.parseInline(trimmed.substring(2)));
                result.add(TextFactory.newline());
                continue;
            }

            result.addAll(InlineParser.parseInline(trimmed));
            result.add(TextFactory.newline());
        }

        if (inCodeBlock && codeBuf.length() > 0) {
            result.add(TextFactory.mkText(codeBuf.toString(), MUTED, FONT, SZ_SMALL));
        }
        return result;
    }

    private MarkdownRenderer() {}
}
