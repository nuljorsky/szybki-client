package pl.szybki.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public final class Draw {
    public static final Identifier FONT = Identifier.of("szybkiclient", "ui");

    public static TextRenderer tr() {
        return MinecraftClient.getInstance().textRenderer;
    }

    private static int fontState = 0; // 0 = nieznany, 1 = wlasna czcionka dziala, 2 = nie dziala

    /** Sprawdza, czy wlasna czcionka sie wczytala (gdy nie, wszystkie znaki to kwadraty o tej samej szerokosci). */
    private static boolean fontOk() {
        if (fontState == 0) {
            try {
                Style st = Style.EMPTY.withFont(FONT);
                int a = tr().getWidth(Text.literal("i").setStyle(st));
                int b = tr().getWidth(Text.literal("W").setStyle(st));
                fontState = (a != b) ? 1 : 2;
            } catch (Throwable t) {
                fontState = 2;
            }
        }
        return fontState == 1;
    }

    public static Text styled(String s, boolean bold) {
        Style st = fontOk() ? Style.EMPTY.withFont(FONT) : Style.EMPTY;
        if (bold) st = st.withBold(true);
        return Text.literal(s).setStyle(st);
    }

    public static float width(String s, float scale, boolean bold) {
        return tr().getWidth(styled(s, bold)) * scale;
    }

    /** Rysuje tekst z lewym gornym rogiem w (x,y). Zwraca szerokosc. */
    public static float text(DrawContext c, String s, float x, float y, int color, float scale, boolean bold, boolean shadow) {
        MatrixStack m = c.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1f);
        Text t = styled(s, bold);
        c.drawText(tr(), t, 0, 0, color, shadow);
        m.pop();
        return tr().getWidth(t) * scale;
    }

    public static void textCentered(DrawContext c, String s, float cx, float y, int color, float scale, boolean bold, boolean shadow) {
        float w = width(s, scale, bold);
        text(c, s, cx - w / 2f, y, color, scale, bold, shadow);
    }

    public static void textRight(DrawContext c, String s, float rx, float y, int color, float scale, boolean bold, boolean shadow) {
        float w = width(s, scale, bold);
        text(c, s, rx - w, y, color, scale, bold, shadow);
    }

    /** Linia o grubosci 'th' miedzy dwoma punktami (w pikselach GUI). */
    public static void line(DrawContext c, float x1, float y1, float x2, float y2, float th, int color) {
        float dx = x2 - x1, dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 0.5f) return;
        MatrixStack m = c.getMatrices();
        m.push();
        m.translate(x1, y1, 0);
        m.multiply(RotationAxis.POSITIVE_Z.rotation((float) Math.atan2(dy, dx)));
        m.translate(0, -th / 2f, 0);
        m.scale(len, th, 1f);
        c.fill(0, 0, 1, 1, color);
        m.pop();
    }

    /** Prostokat z "sciêtymi" rogami. */
    public static void chamfer(DrawContext c, int x, int y, int w, int h, int color) {
        c.fill(x + 1, y, x + w - 1, y + h, color);
        c.fill(x, y + 1, x + w, y + h - 1, color);
    }

    public static void box(DrawContext c, int x, int y, int w, int h, int fill, int border) {
        chamfer(c, x, y, w, h, border);
        chamfer(c, x + 1, y + 1, w - 2, h - 2, fill);
    }

    /** Minecraftowy miecz (pixel art), srodek w (cx,cy), pionowo ostrzem w gore, obrocony o rot stopni. */
    public static void sword(DrawContext c, float cx, float cy, float px, float rotDeg, boolean dark, int alpha) {
        int edge, hi, guard, handle;
        if (dark) {
            edge = 0x111111;
            hi = 0x555555;
            guard = 0x000000;
            handle = 0x2A2A2A;
        } else {
            edge = 0x8C94A3;
            hi = 0xF4F6FA;
            guard = 0x6B7380;
            handle = 0xB4BBC7;
        }
        int a = alpha << 24;
        MatrixStack m = c.getMatrices();
        m.push();
        m.translate(cx, cy, 0);
        m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotDeg));
        m.scale(px, px, 1f);
        c.fill(0, -12, 1, -11, a | edge);       // czubek
        c.fill(-1, -11, 2, 2, a | edge);        // ostrze
        c.fill(0, -10, 1, 2, a | hi);           // blask ostrza
        c.fill(-4, 2, 5, 4, a | guard);         // jelec
        c.fill(-1, 4, 2, 8, a | handle);        // rekojesc
        c.fill(-2, 8, 3, 10, a | guard);        // glowica
        m.pop();
    }

    private Draw() {}
}
