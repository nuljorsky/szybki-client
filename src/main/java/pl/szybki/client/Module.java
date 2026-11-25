package pl.szybki.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public record Action(String key, Runnable run) {}

    public final String id;
    public final Category category;
    public boolean enabled;
    public final List<Setting> settings = new ArrayList<>();
    public final List<Action> actions = new ArrayList<>();
    public String hintKey;

    protected Module(String id, Category category) {
        this.id = id;
        this.category = category;
    }

    public String name() {
        return Lang.t("mod." + id + ".name");
    }

    public String desc() {
        return Lang.t("mod." + id + ".desc");
    }

    /** Czy stan wlaczenia ma byc zapisywany w configu. */
    public boolean persists() {
        return true;
    }

    public boolean hasOptions() {
        return !settings.isEmpty() || !actions.isEmpty() || hintKey != null;
    }

    public void setEnabled(boolean e) {
        if (e == enabled) return;
        enabled = e;
        if (e) onEnable();
        else onDisable();
        Config.save();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void onEnable() {}

    public void onDisable() {}

    /** Wywolywane na poczatku kazdego ticka klienta (zawsze, takze gdy modul wylaczony). */
    public void onTick(MinecraftClient mc) {}

    /** Wywolywane tylko gdy modul wlaczony, w trakcie rysowania HUD. */
    public void onHud(DrawContext ctx, float tickDelta) {}

    protected Setting num(String key, double def, double min, double max, double step) {
        Setting s = Setting.num(key, def, min, max, step);
        settings.add(s);
        return s;
    }

    protected Setting flag(String key, boolean def) {
        Setting s = Setting.flag(key, def);
        settings.add(s);
        return s;
    }

    public double d(String key) {
        for (Setting s : settings) if (s.key.equals(key)) return s.value;
        return 0;
    }

    public boolean b(String key) {
        for (Setting s : settings) if (s.key.equals(key)) return s.flag;
        return false;
    }
}
