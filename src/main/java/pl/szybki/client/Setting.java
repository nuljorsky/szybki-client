package pl.szybki.client;

import java.util.Locale;

public final class Setting {
    public final String key;
    public final boolean isBool;
    public double value;
    public boolean flag;
    public final double min, max, step;

    private Setting(String key, boolean isBool, double def, double min, double max, double step, boolean flagDef) {
        this.key = key;
        this.isBool = isBool;
        this.value = def;
        this.min = min;
        this.max = max;
        this.step = step;
        this.flag = flagDef;
    }

    public static Setting num(String key, double def, double min, double max, double step) {
        return new Setting(key, false, def, min, max, step, false);
    }

    public static Setting flag(String key, boolean def) {
        return new Setting(key, true, 0, 0, 1, 1, def);
    }

    public String label() {
        return Lang.t("set." + key);
    }

    public void change(int dir) {
        double v = value + dir * step;
        v = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, v));
    }

    public String display() {
        if (isBool) return flag ? "ON" : "OFF";
        if (step >= 1.0) return String.valueOf((int) Math.round(value));
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
