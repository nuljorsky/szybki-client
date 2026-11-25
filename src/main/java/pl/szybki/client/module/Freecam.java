package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import pl.szybki.client.Category;
import pl.szybki.client.Module;

public class Freecam extends Module {
    public static Freecam I;

    public double x, y, z, px, py, pz;
    public float yaw, pitch;

    private Input saved;
    private final Input dummy = new Input();

    public Freecam() {
        super("freecam", Category.CHEATS);
        I = this;
        num("speed", 1.0, 0.2, 5.0, 0.2);
    }

    public static boolean active() {
        return I != null && I.enabled;
    }

    @Override
    public boolean persists() {
        return false;
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) {
            enabled = false;
            return;
        }
        Vec3d p = mc.gameRenderer.getCamera().getPos();
        x = px = p.x;
        y = py = p.y;
        z = pz = p.z;
        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();
        if (mc.player.input != dummy) saved = mc.player.input;
        mc.player.input = dummy;
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && saved != null && mc.player.input == dummy) {
            mc.player.input = saved;
        }
        saved = null;
    }

    public void look(double dx, double dy) {
        yaw += (float) (dx * 0.15);
        pitch = MathHelper.clamp(pitch + (float) (dy * 0.15), -90f, 90f);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!enabled) return;
        if (mc.player == null || mc.world == null) {
            enabled = false;
            saved = null;
            return;
        }
        if (mc.player.input != dummy) {
            saved = mc.player.input;
            mc.player.input = dummy;
        }
        px = x;
        py = y;
        pz = z;
        if (mc.currentScreen != null) return;

        GameOptions o = mc.options;
        double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
        double fx = -Math.sin(yr) * Math.cos(pr);
        double fy = -Math.sin(pr);
        double fz = Math.cos(yr) * Math.cos(pr);
        double rx = -Math.cos(yr), rz = -Math.sin(yr);

        double mx = 0, my = 0, mz = 0;
        if (o.forwardKey.isPressed()) { mx += fx; my += fy; mz += fz; }
        if (o.backKey.isPressed()) { mx -= fx; my -= fy; mz -= fz; }
        if (o.rightKey.isPressed()) { mx += rx; mz += rz; }
        if (o.leftKey.isPressed()) { mx -= rx; mz -= rz; }
        if (o.jumpKey.isPressed()) my += 1;
        if (o.sneakKey.isPressed()) my -= 1;

        double len = Math.sqrt(mx * mx + my * my + mz * mz);
        if (len > 1e-6) {
            double speed = d("speed") * (o.sprintKey.isPressed() ? 2.0 : 1.0);
            x += mx / len * speed;
            y += my / len * speed;
            z += mz / len * speed;
        }
    }
}
