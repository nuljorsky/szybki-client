package pl.szybki.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Wlasna projekcja swiat -> ekran (wspolrzedne GUI). Dzieki temu ESP/tracery/nametagi
 * rysujemy w 2D bez grzebania w pipeline renderowania 3D.
 */
public final class Projection {
    public static double cx, cy, cz;
    public static int sw, sh;
    private static double fx, fy, fz, rx, rz, ux, uy, uz;
    private static double tanHalf, aspect;

    public static void update(MinecraftClient mc, float td) {
        Camera cam = mc.gameRenderer.getCamera();
        Vec3d p = cam.getPos();
        cx = p.x;
        cy = p.y;
        cz = p.z;
        double yr = Math.toRadians(cam.getYaw());
        double pr = Math.toRadians(cam.getPitch());
        fx = -Math.sin(yr) * Math.cos(pr);
        fy = -Math.sin(pr);
        fz = Math.cos(yr) * Math.cos(pr);
        rx = -Math.cos(yr);
        rz = -Math.sin(yr);
        // up = right x forward  (ry = 0)
        ux = -rz * fy;
        uy = rz * fx - rx * fz;
        uz = rx * fy;

        sw = mc.getWindow().getScaledWidth();
        sh = mc.getWindow().getScaledHeight();
        aspect = (double) sw / (double) sh;

        double fov = mc.options.getFov().getValue();
        ClientPlayerEntity pl = mc.player;
        if (pl != null) {
            float f = 1.0f;
            if (pl.getAbilities().flying) f *= 1.1f;
            float walk = pl.getAbilities().getWalkSpeed();
            if (walk > 0) f *= (pl.getMovementSpeed() / walk + 1.0f) / 2.0f;
            double scale = mc.options.getFovEffectScale().getValue();
            f = MathHelper.lerp((float) scale, 1.0f, f);
            fov *= f;
        }
        tanHalf = Math.tan(Math.toRadians(fov) / 2.0);
    }

    /** Wspolrzedne w przestrzeni kamery: {prawo, gora, glebokosc}. */
    public static double[] cam(double x, double y, double z) {
        double dx = x - cx, dy = y - cy, dz = z - cz;
        return new double[]{
                dx * rx + dz * rz,
                dx * ux + dy * uy + dz * uz,
                dx * fx + dy * fy + dz * fz
        };
    }

    private static double sx(double[] c) {
        return sw / 2.0 + (c[0] / c[2]) / (tanHalf * aspect) * (sw / 2.0);
    }

    private static double sy(double[] c) {
        return sh / 2.0 - (c[1] / c[2]) / tanHalf * (sh / 2.0);
    }

    /** Rzutuje punkt. out = {x, y, glebokosc}. false gdy za kamera. */
    public static boolean project(double x, double y, double z, double[] out) {
        double[] c = cam(x, y, z);
        if (c[2] < 0.05) return false;
        out[0] = sx(c);
        out[1] = sy(c);
        out[2] = c[2];
        return true;
    }

    /** Rzutuje odcinek z przycieciem do plaszczyzny bliskiej. out = {x1,y1,x2,y2}. */
    public static boolean line(double x1, double y1, double z1, double x2, double y2, double z2, double[] out) {
        final double near = 0.05;
        double[] a = cam(x1, y1, z1);
        double[] b = cam(x2, y2, z2);
        if (a[2] < near && b[2] < near) return false;
        if (a[2] < near) {
            double t = (near - a[2]) / (b[2] - a[2]);
            a = new double[]{a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t, near};
        } else if (b[2] < near) {
            double t = (near - b[2]) / (a[2] - b[2]);
            b = new double[]{b[0] + (a[0] - b[0]) * t, b[1] + (a[1] - b[1]) * t, near};
        }
        out[0] = sx(a);
        out[1] = sy(a);
        out[2] = sx(b);
        out[3] = sy(b);
        return true;
    }

    private Projection() {}
}
