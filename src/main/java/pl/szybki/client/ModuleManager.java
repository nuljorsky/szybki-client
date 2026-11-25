package pl.szybki.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import pl.szybki.client.module.*;
import pl.szybki.client.util.Hud;
import pl.szybki.client.util.Projection;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    public static final List<Module> ALL = new ArrayList<>();

    public static Hitboxes HITBOXES;
    public static FastPlace FASTPLACE;
    public static Freecam FREECAM;
    public static Radar RADAR;
    public static Tracers TRACERS;
    public static BlockEsp BLOCKESP;
    public static Nametags NAMETAGS;
    public static ElytraDetector ELYTRA;
    public static AntyKostka ANTYKOSTKA;
    public static AutoNielot AUTONIELOT;
    public static AutoLeave AUTOLEAVE;
    public static NameProtect NAMEPROTECT;

    public static void init() {
        // Kolejnosc rejestracji = kolejnosc rysowania HUD.
        // CHEATY
        ALL.add(HITBOXES = new Hitboxes());
        ALL.add(FASTPLACE = new FastPlace());
        ALL.add(FREECAM = new Freecam());
        // RADAR
        ALL.add(RADAR = new Radar());
        ALL.add(TRACERS = new Tracers());
        ALL.add(BLOCKESP = new BlockEsp());
        ALL.add(NAMETAGS = new Nametags());
        ALL.add(ELYTRA = new ElytraDetector());
        // WSPOMAGACZE
        ALL.add(ANTYKOSTKA = new AntyKostka());
        ALL.add(AUTONIELOT = new AutoNielot());
        ALL.add(AUTOLEAVE = new AutoLeave());
        ALL.add(NAMEPROTECT = new NameProtect());
    }

    public static List<Module> of(Category c) {
        List<Module> r = new ArrayList<>();
        for (Module m : ALL) if (m.category == c) r.add(m);
        return r;
    }

    public static void tick(MinecraftClient mc) {
        for (Module m : ALL) {
            try {
                m.onTick(mc);
            } catch (Throwable t) {
                System.err.println("[SZYBKI CLIENT] Blad w module " + m.id + ": " + t);
            }
        }
    }

    public static void renderHud(DrawContext ctx, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;
        Projection.update(mc, td);
        for (Module m : ALL) {
            if (!m.enabled) continue;
            try {
                m.onHud(ctx, td);
            } catch (Throwable t) {
                System.err.println("[SZYBKI CLIENT] Blad HUD w module " + m.id + ": " + t);
            }
        }
        Hud.render(ctx);
    }

    private ModuleManager() {}
}
