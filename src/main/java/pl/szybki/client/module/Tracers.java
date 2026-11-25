package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import pl.szybki.client.Category;
import pl.szybki.client.Module;
import pl.szybki.client.util.Draw;
import pl.szybki.client.util.Projection;
import pl.szybki.client.util.Util;

/** Linie od srodka ekranu do graczy (domyslnie do 100 m). */
public class Tracers extends Module {
    public Tracers() {
        super("tracers", Category.RADAR);
        num("range", 100, 25, 200, 25);
    }

    @Override
    public void onHud(DrawContext c, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        double range = d("range");
        float cx = Projection.sw / 2f, cy = Projection.sh / 2f;
        double[] out = new double[3];
        for (AbstractClientPlayerEntity p : Util.others(mc)) {
            Vec3d pp = p.getLerpedPos(td);
            double ty = pp.y + p.getHeight() * 0.5;
            double dist = Math.sqrt(Math.pow(pp.x - Projection.cx, 2) + Math.pow(ty - Projection.cy, 2) + Math.pow(pp.z - Projection.cz, 2));
            if (dist > range) continue;
            float t = (float) Math.min(1.0, dist / range);
            int col = Util.lerpColor(0xFFFF2D2D, 0xFF2DD4FF, t);
            if (Util.gliding(p)) col = 0xFFFFC400;

            float ex, ey;
            if (Projection.project(pp.x, ty, pp.z, out)) {
                ex = (float) out[0];
                ey = (float) out[1];
            } else {
                double[] cs = Projection.cam(pp.x, ty, pp.z);
                double dx = cs[0], dy = -cs[1];
                double l = Math.sqrt(dx * dx + dy * dy);
                if (l < 1e-4) {
                    dx = 0;
                    dy = 1;
                    l = 1;
                }
                ex = (float) (cx + dx / l * 3000);
                ey = (float) (cy + dy / l * 3000);
            }
            Draw.line(c, cx, cy, ex, ey, 0.9f, col);
        }
    }
}
