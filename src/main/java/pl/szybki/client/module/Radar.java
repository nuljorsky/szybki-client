package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import pl.szybki.client.Category;
import pl.szybki.client.Module;
import pl.szybki.client.util.Draw;
import pl.szybki.client.util.Util;

/** Radar graczy - gora = kierunek, w ktorym patrzysz. */
public class Radar extends Module {
    public Radar() {
        super("radar", Category.RADAR);
        num("range", 100, 25, 200, 25);
        num("size", 100, 70, 150, 10);
    }

    @Override
    public void onHud(DrawContext c, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int size = (int) d("size");
        double range = d("range");
        int x = 4, y = 24;
        int cx = x + size / 2, cy = y + size / 2;
        float r = size / 2f - 3f;

        c.fill(x, y, x + size, y + size, 0xB0101419);
        c.fill(x, y, x + size, y + 1, 0xFFE5322D);
        // siatka
        c.fill(cx, y + 1, cx + 1, y + size, 0x30FFFFFF);
        c.fill(x, cy, x + size, cy + 1, 0x30FFFFFF);
        for (int i = 1; i <= 2; i++) {
            float rr = r * i / 2f;
            Draw.line(c, cx - rr, cy - rr, cx + rr, cy - rr, 0.5f, 0x28FFFFFF);
            Draw.line(c, cx - rr, cy + rr, cx + rr, cy + rr, 0.5f, 0x28FFFFFF);
            Draw.line(c, cx - rr, cy - rr, cx - rr, cy + rr, 0.5f, 0x28FFFFFF);
            Draw.line(c, cx + rr, cy - rr, cx + rr, cy + rr, 0.5f, 0x28FFFFFF);
        }

        double yaw = Math.toRadians(mc.gameRenderer.getCamera().getYaw());
        double fx = -Math.sin(yaw), fz = Math.cos(yaw);
        double rx = -Math.cos(yaw), rz = -Math.sin(yaw);

        // N (polnoc = -Z)
        double na = 0 * rx + (-1) * rz;
        double nb = 0 * fx + (-1) * fz;
        Draw.textCentered(c, "N", (float) (cx + na * (r + 0.5f)) - 0.5f, (float) (cy - nb * (r + 0.5f)) - 4f, 0xFFE5322D, 0.8f, true, false);

        Vec3d me = mc.player.getPos();
        for (AbstractClientPlayerEntity p : Util.others(mc)) {
            Vec3d pp = p.getLerpedPos(td);
            double dx = pp.x - me.x, dz = pp.z - me.z;
            double a = dx * rx + dz * rz;
            double b = dx * fx + dz * fz;
            double dist = Math.sqrt(dx * dx + dz * dz);
            double k = r / range;
            double px = a * k, py = b * k;
            boolean clamped = false;
            double l = Math.sqrt(px * px + py * py);
            if (l > r) {
                px *= r / l;
                py *= r / l;
                clamped = true;
            }
            int col = Util.gliding(p) ? 0xFFFFC400 : (dist < 30 ? 0xFFFF3B30 : 0xFFFF7A59);
            if (clamped) col = Util.alpha(col, 140);
            int sx = (int) Math.round(cx + px);
            int sy = (int) Math.round(cy - py);
            c.fill(sx - 2, sy - 2, sx + 3, sy + 3, 0xFF000000);
            c.fill(sx - 1, sy - 1, sx + 2, sy + 2, col);
        }
        // ja
        c.fill(cx - 1, cy - 2, cx + 2, cy + 3, 0xFFFFFFFF);
        c.fill(cx - 2, cy - 1, cx + 3, cy + 2, 0xFFFFFFFF);
        Draw.text(c, (int) range + "m", x + 3, y + size - 9, 0xFF9AA3B2, 0.7f, false, false);
    }
}
