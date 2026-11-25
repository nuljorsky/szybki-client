package pl.szybki.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class Util {
    public static List<AbstractClientPlayerEntity> others(MinecraftClient mc) {
        List<AbstractClientPlayerEntity> r = new ArrayList<>();
        if (mc.world == null || mc.player == null) return r;
        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator() || p.isRemoved()) continue;
            r.add(p);
        }
        return r;
    }

    /** Wykrywa lot elytra po nazwie pozy (odporne na zmiany nazw w mappingach). */
    public static boolean gliding(PlayerEntity p) {
        String n = p.getPose().name();
        return n.equals("FALL_FLYING") || n.equals("GLIDING");
    }

    /** Indeks ekwipunku (0-8 hotbar, 9-35 glowny) -> id slotu w PlayerScreenHandler. */
    public static int slotId(int invIdx) {
        return invIdx < 9 ? 36 + invIdx : invIdx;
    }

    public static int lerpColor(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int aa = (int) (((a >>> 24) & 255) + (((b >>> 24) & 255) - ((a >>> 24) & 255)) * t);
        int ar = (int) (((a >> 16) & 255) + (((b >> 16) & 255) - ((a >> 16) & 255)) * t);
        int ag = (int) (((a >> 8) & 255) + (((b >> 8) & 255) - ((a >> 8) & 255)) * t);
        int ab = (int) ((a & 255) + ((b & 255) - (a & 255)) * t);
        return (aa << 24) | (ar << 16) | (ag << 8) | ab;
    }

    public static int alpha(int color, int a) {
        return (Math.max(0, Math.min(255, a)) << 24) | (color & 0xFFFFFF);
    }

    public static float ease(float t) {
        t = Math.max(0f, Math.min(1f, t));
        float u = 1f - t;
        return 1f - u * u * u;
    }

    public static void msg(String s) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.literal("[SZYBKI] " + s), true);
    }

    private Util() {}
}
