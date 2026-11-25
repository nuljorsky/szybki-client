package pl.szybki.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import pl.szybki.client.Config;
import pl.szybki.client.Module;
import pl.szybki.client.ModuleManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Znak wodny + lista wlaczonych modulow. */
public final class Hud {
    public static final int ACCENT = 0xFFE5322D;

    public static void render(DrawContext c) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (Config.hudMark) {
            String a = "SZYBKI";
            String b = " CLIENT";
            String fps = "  " + mc.getCurrentFps() + " FPS";
            float wa = Draw.width(a, 1f, true);
            float wb = Draw.width(b, 1f, true);
            float wf = Draw.width(fps, 0.85f, false);
            int w = (int) (wa + wb + wf) + 12;
            c.fill(4, 4, 4 + w, 18, 0xC0101419);
            c.fill(4, 4, 6, 18, ACCENT);
            Draw.text(c, a, 10, 7, 0xFFFFFFFF, 1f, true, false);
            Draw.text(c, b, 10 + wa, 7, ACCENT, 1f, true, false);
            Draw.text(c, fps, 10 + wa + wb, 8, 0xFFB5BCC8, 0.85f, false, false);
        }
        if (Config.hudList) {
            List<Module> on = new ArrayList<>();
            for (Module m : ModuleManager.ALL) if (m.enabled) on.add(m);
            on.sort(Comparator.comparingDouble((Module m) -> -Draw.width(m.name(), 0.9f, true)));
            int sw = mc.getWindow().getScaledWidth();
            int y = 4;
            for (Module m : on) {
                String n = m.name();
                float w = Draw.width(n, 0.9f, true);
                int x2 = sw - 4;
                int x1 = (int) (x2 - w - 10);
                c.fill(x1, y, x2, y + 12, 0xB0101419);
                c.fill(x2 - 2, y, x2, y + 12, ACCENT);
                Draw.text(c, n, x1 + 4, y + 2.5f, 0xFFFFFFFF, 0.9f, true, false);
                y += 12;
            }
        }
    }

    private Hud() {}
}
