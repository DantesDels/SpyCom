package ui;

import javafx.scene.text.FontWeight;

public final class Theme {
    private Theme() {}

    // ═══════════════════════════════════════════
    //  COLOR PALETTE
    // ═══════════════════════════════════════════
    public static final String BG_DARK   = "#080808";
    public static final String BG_PANEL  = "#131314";
    public static final String BG_INPUT  = "#171725";
    public static final String BG_TEXTAREA = "#0a0a0a";
    public static final String ACCENT    = "#00ff88";
    public static final String ACCENT_DIM= "#00aa55";
    public static final String TEXT      = "#c0c0c0";
    public static final String MUTED     = "#606078";
    public static final String DANGER    = "#ff4444";
    public static final String AMBER     = "#ffaa00";
    public static final String PRIVATE   = "#ff66cc";
    public static final String CYAN      = "#00ffff";
    public static final String BORDER    = "#2a2a3e";

    // Status → color mapping
    public static String statusColor(String s) {
        return switch (s) {
            case "online" -> ACCENT;
            case "busy"   -> DANGER;
            case "afk"    -> AMBER;
            default       -> CYAN;
        };
    }

    // ═══════════════════════════════════════════
    //  TYPOGRAPHY
    // ═══════════════════════════════════════════
    public static final String FONT       = "Consolas";
    public static final String FONT_STACK = "Consolas,monospace";

    // Named sizes (change them all here)
    public static final double SZ_TINY    = 10;
    public static final double SZ_SMALL   = 11;
    public static final double SZ_NORMAL  = 12;
    public static final double SZ_MEDIUM  = 13;
    public static final double SZ_LARGE   = 14;

    // ═══════════════════════════════════════════
    //  SHORTCUTS — chainable helpers
    // ═══════════════════════════════════════════

    public static String bg(String c) {
        return "-fx-background-color: " + c + ";";
    }

    public static String bg(String c, String radius) {
        return "-fx-background-color: " + c + "; -fx-background-radius: " + radius + ";";
    }

    public static String text(String c) {
        return "-fx-text-fill: " + c + ";";
    }

    public static String font(String family, double size) {
        return "-fx-font-family: " + family + "; -fx-font-size: " + size + "px;";
    }

    public static String font(String family, FontWeight weight, double size) {
        return "-fx-font-family: " + family + "; -fx-font-weight: " +
               weight.name().toLowerCase() + "; -fx-font-size: " + size + "px;";
    }

    public static String fg(String color, String family, double size) {
        return text(color) + font(family, size);
    }

    public static String border(String c) {
        return "-fx-border-color: " + c + ";";
    }

    public static String border(String c, String radius) {
        return "-fx-border-color: " + c + "; -fx-border-radius: " + radius + ";";
    }

    public static String border(String c, String width, String radius) {
        return "-fx-border-color: " + c + "; -fx-border-width: " + width + "; -fx-border-radius: " + radius + ";";
    }

    public static String padding(double top, double right, double bottom, double left) {
        return "-fx-padding: " + top + " " + right + " " + bottom + " " + left + ";";
    }

    public static String padding(double v) {
        return "-fx-padding: " + v + ";";
    }

    public static String padding(double y, double x) {
        return "-fx-padding: " + y + " " + x + ";";
    }

    /** ListView background — removes focus ring */
    public static String listBg(String c) {
        return bg(c) + "-fx-control-inner-background: " + c + "; -fx-focus-color: transparent;";
    }

    /** Rounded input-like surface */
    public static String surface(String bg, String borderColor, double radius) {
        return bg(bg, radius + "") + border(borderColor, radius + "");
    }

    /** Button with hover effect — returns array [base, hover] */
    public static String[] btn(String bg, String bgHover, String textColor, String family, double size) {
        String base = "-fx-background-color: " + bg + "; " + fg(textColor, family, size) +
                      "-fx-background-radius: 4px; -fx-padding: 8 18; -fx-cursor: hand;";
        String hover = base.replace(bg, bgHover);
        return new String[]{base, hover};
    }

    // ═══════════════════════════════════════════
    //  PREDEFINED COMPOUND STYLES
    // ═══════════════════════════════════════════

    public static String dotStyle(String color) {
        return text(color) + "-fx-font-size: 14px;";
    }

    public static String style(String... props) {
        return String.join("; ", props);
    }

    public static String cursor(String c) {
        return "-fx-cursor: " + c + ";";
    }
}
