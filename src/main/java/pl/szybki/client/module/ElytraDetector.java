package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import pl.szybki.client.Category;
import pl.szybki.client.Lang;
import pl.szybki.client.Module;
import pl.szybki.client.util.Draw;
import pl.szybki.client.util.Util;

/** Duzy czerwony napis na gorze ekranu, gdy ktos leci elytra w poblizu. */
public class ElytraDetector extends Module {
    public ElytraDetector() {
        super("elytra", Category.RADAR);
        num("range", 100, 25, 200, 25);
    }

    @Override
    public void onHud(DrawContext c, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        double range = d("range");
        AbstractClientPlayerEntity nearest = null;
        double best = Double.MAX_VALUE;
        int count = 0;
        for (AbstractClientPlayerEntity p : Util.others(mc)) {
            if (!Util.gliding(p)) continue;
            double dist = mc.player.distanceTo(p);
            if (dist > range) continue;
            count++;
            if (dist < best) {
                best = dist;
                nearest = p;
            }
        }
        if (nearest == null) return;

        int sw = mc.getWindow().getScaledWidth();
        float pulse = 0.5f + 0.5f * (float) Math.sin(System.nanoTime() / 1e9 * 9.0);
        int a = (int) (90 + 100 * pulse);

        // czerwona poswiata u gory
        c.fillGradient(0, 0, sw, 70, Util.alpha(0xFF0000, a), 0x00FF0000);

        String msg = Lang.t("alert.elytra");
        float scale = 3.0f + pulse * 0.25f;
        float w = Draw.width(msg, scale, true);
        float x = (sw - w) / 2f;
        Draw.text(c, msg, x + 1.5f, 11.5f, 0xFF330000, scale, true, false);
        Draw.text(c, msg, x, 10f, 0xFFFF1A1A, scale, true, false);

        String sub = nearest.getName().getString() + "  -  " + Math.round(best) + "m" + (count > 1 ? "  (+" + (count - 1) + ")" : "");
        Draw.textCentered(c, sub, sw / 2f, 10f + 9f * scale + 4f, 0xFFFFFFFF, 1.1f, true, true);
    }
}
