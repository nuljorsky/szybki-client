package pl.szybki.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pl.szybki.client.Category;
import pl.szybki.client.Config;
import pl.szybki.client.Lang;
import pl.szybki.client.Module;
import pl.szybki.client.ModuleManager;
import pl.szybki.client.Setting;
import pl.szybki.client.util.Draw;
import pl.szybki.client.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Glowne menu SZYBKI CLIENT: jasny motyw, animacje, latajace miecze w tle. */
public class ClientScreen extends Screen {
    // wirtualny rozmiar panelu (skalowany do okna)
    private static final int W = 440, H = 256, SIDE = 112;

    private static final int PANEL = 0xFFFFFFFF;
    private static final int SIDEBG = 0xFFF3F5F9;
    private static final int LINE = 0xFFE1E5EC;
    private static final int TXT = 0xFF12161C;
    private static final int MUTED = 0xFF6A7382;
    private static final int ACC = 0xFFE5322D;
    private static final int CARD = 0xFFF8F9FC;

    private record Hit(int x1, int y1, int x2, int y2, Runnable run) {}

    private final SwordField field = new SwordField();
    private final List<Hit> hits = new ArrayList<>();
    private final Map<String, Float> anim = new HashMap<>();

    private int tab = 0; // 0..2 kategorie, 3 ustawienia
    private Module sel = null;
    private float open = 0f, tabY = -1f, selAnim = 0f, tabSwitch = 0f;
    private float scroll = 0f, scrollT = 0f;
    private long last = System.nanoTime();

    private float s = 1f, ox, oy;
    private double vmx, vmy;

    public ClientScreen() {
        super(Text.literal("SZYBKI CLIENT"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext c, int mouseX, int mouseY, float delta) {
        field.render(c, this.width, this.height);
    }

    // ---------- pomocnicze ----------

    private float an(String key, float target, float dt, float speed) {
        float v = anim.getOrDefault(key, target);
        v += (target - v) * Math.min(1f, dt * speed);
        if (Math.abs(target - v) < 0.002f) v = target;
        anim.put(key, v);
        return v;
    }

    private boolean over(int x1, int y1, int x2, int y2) {
        return vmx >= x1 && vmx < x2 && vmy >= y1 && vmy < y2;
    }

    private void hit(int x1, int y1, int x2, int y2, Runnable r) {
        hits.add(new Hit(x1, y1, x2, y2, r));
    }

    private void toggleSwitch(DrawContext c, int x, int y, float a) {
        int track = Util.lerpColor(0xFFD3D8E2, ACC, a);
        Draw.box(c, x, y, 26, 12, track, Util.lerpColor(0xFFC3C9D4, 0xFFB5231F, a));
        int kx = (int) (x + 2 + 14 * a);
        c.fill(kx, y + 2, kx + 8, y + 10, 0xFFFFFFFF);
    }

    private List<String> wrap(String text, float maxW, float scale) {
        List<String> lines = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = cur.length() == 0 ? word : cur + " " + word;
            if (Draw.width(test, scale, false) > maxW && cur.length() > 0) {
                lines.add(cur.toString());
                cur = new StringBuilder(word);
            } else {
                cur = new StringBuilder(test);
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }

    // ---------- render ----------

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - last) / 1e9f);
        last = now;

        open = Math.min(1f, open + dt * 3.2f);
        tabSwitch += dt;
        float e = Util.ease(open);
        float tabTarget = 46 + tab * 30;
        if (tabY < 0) tabY = tabTarget;
        tabY += (tabTarget - tabY) * Math.min(1f, dt * 14f);
        selAnim += ((sel != null ? 1f : 0f) - selAnim) * Math.min(1f, dt * 12f);
        if (Math.abs((sel != null ? 1f : 0f) - selAnim) < 0.003f) selAnim = sel != null ? 1f : 0f;
        scroll += (scrollT - scroll) * Math.min(1f, dt * 14f);

        float fit = Math.min((float) this.width / (W + 24f), (float) this.height / (H + 24f));
        s = Math.max(0.5f, Math.min(1.6f, fit)) * (0.92f + 0.08f * e);
        ox = (this.width - W * s) / 2f;
        oy = (this.height - H * s) / 2f + (1f - e) * 18f;
        vmx = (mouseX - ox) / s;
        vmy = (mouseY - oy) / s;
        hits.clear();

        MatrixStack m = c.getMatrices();
        m.push();
        m.translate(ox, oy, 0);
        m.scale(s, s, 1f);

        // cien + panel
        c.fill(-4, -3, W + 4, H + 5, 0x14000000);
        c.fill(-2, -1, W + 2, H + 3, 0x1C000000);
        c.fill(0, 0, W, H, PANEL);

        drawSidebar(c, dt);
        drawContent(c, dt);
        drawHeader(c);

        // ramka
        c.fill(0, 0, W, 1, LINE);
        c.fill(0, H - 1, W, H, LINE);
        c.fill(0, 0, 1, H, LINE);
        c.fill(W - 1, 0, W, H, LINE);

        m.pop();
    }

    private void drawHeader(DrawContext c) {
        c.fill(1, 1, W - 1, 36, 0xFFFFFFFF);
        c.fill(0, 36, W, 37, LINE);
        c.fill(0, 0, W, 2, ACC);
        Draw.sword(c, 20, 20, 1.2f, 45f, true, 255);
        float x = 40;
        x += Draw.text(c, "SZYBKI", x, 10, TXT, 1.6f, true, false);
        Draw.text(c, " CLIENT", x, 10, ACC, 1.6f, true, false);
        Draw.textRight(c, "v1.0.0  •  1.21.4", W - 40, 14, MUTED, 0.8f, false, false);

        boolean ho = over(W - 28, 8, W - 8, 28);
        c.fill(W - 28, 8, W - 8, 28, ho ? ACC : 0xFFF1F3F7);
        Draw.textCentered(c, "×", W - 18, 12, ho ? 0xFFFFFFFF : TXT, 1.3f, true, false);
        hit(W - 28, 8, W - 8, 28, this::close);
    }

    private void drawSidebar(DrawContext c, float dt) {
        c.fill(1, 37, SIDE, H - 1, SIDEBG);
        c.fill(SIDE, 37, SIDE + 1, H - 1, LINE);

        // aktywny wskaznik (plynnie sie przesuwa)
        int ty = (int) tabY;
        Draw.box(c, 8, ty, SIDE - 16, 26, 0xFFFFFFFF, LINE);
        c.fill(8, ty + 5, 11, ty + 21, ACC);

        String[] labels = {
                Category.CHEATS.tab(), Category.RADAR.tab(), Category.HELPERS.tab(), Lang.t("tab.settings")
        };
        for (int i = 0; i < 4; i++) {
            int y = 46 + i * 30;
            boolean act = tab == i;
            boolean ho = over(8, y, SIDE - 8, y + 26);
            float h = an("tab" + i, ho ? 1f : 0f, dt, 14f);
            int col = act ? TXT : Util.lerpColor(MUTED, TXT, h);
            if (!act && h > 0.01f) c.fill(8, y, SIDE - 8, y + 26, Util.alpha(0x000000, (int) (10 * h)));
            Draw.text(c, labels[i], 20 + (act ? 2 : 0), y + 8, col, 0.9f, act, false);
            final int idx = i;
            hit(8, y, SIDE - 8, y + 26, () -> {
                if (tab != idx) {
                    tab = idx;
                    sel = null;
                    scroll = scrollT = 0;
                    tabSwitch = 0;
                }
            });
        }
        Draw.text(c, Lang.t("ui.hint"), 12, H - 18, MUTED, 0.72f, false, false);
    }

    private void drawContent(DrawContext c, float dt) {
        int cx = SIDE + 14;
        int cw = W - cx - 12;
        if (tab == 3) {
            drawSettingsTab(c, dt, cx, cw);
            return;
        }
        if (sel != null && selAnim > 0.5f) {
            drawModuleSettings(c, dt, cx, cw);
            return;
        }
        drawCards(c, dt, cx, cw);
    }

    private void drawCards(DrawContext c, float dt, int cx, int cw) {
        Category cat = Category.values()[tab];
        List<Module> mods = ModuleManager.of(cat);
        int top = 74, bottom = H - 10;
        int cols = 2, gap = 8;
        int cwid = (cw - gap) / cols;
        int ch = 64;
        int rows = (mods.size() + cols - 1) / cols;
        int contentH = rows * (ch + gap);
        float maxScroll = Math.max(0, contentH - (bottom - top));
        scrollT = Math.max(0, Math.min(maxScroll, scrollT));

        float slideIn = (1f - selAnim) < 1f ? (1f - selAnim) : 1f;
        for (int i = 0; i < mods.size(); i++) {
            Module mod = mods.get(i);
            int x = cx + (i % cols) * (cwid + gap);
            float stag = Util.ease(tabSwitch * 4f - i * 0.35f);
            int y = top + (i / cols) * (ch + gap) - (int) scroll + (int) ((1f - stag) * 14f);
            if (y + ch < top - 2 || y > bottom + 2) continue;

            boolean inView = vmy >= top && vmy <= bottom;
            boolean ho = inView && over(x, y, x + cwid, y + ch);
            float h = an("card." + mod.id, ho ? 1f : 0f, dt, 14f);
            float on = an("on." + mod.id, mod.enabled ? 1f : 0f, dt, 12f);

            int bg = Util.lerpColor(CARD, 0xFFFFFFFF, h);
            int border = Util.lerpColor(Util.lerpColor(LINE, 0xFFF0B9B7, on), ACC, h * 0.8f);
            Draw.box(c, x, y, cwid, ch, bg, border);
            if (on > 0.01f) c.fill(x + 1, y + 6, x + 3, y + ch - 6, Util.alpha(ACC & 0xFFFFFF, (int) (255 * on)));

            Draw.text(c, mod.name(), x + 10, y + 8, TXT, 0.98f, true, false);
            List<String> lines = wrap(mod.desc(), cwid - 20, 0.72f);
            for (int l = 0; l < Math.min(3, lines.size()); l++) {
                Draw.text(c, lines.get(l), x + 10, y + 21 + l * 8.5f, MUTED, 0.72f, false, false);
            }
            toggleSwitch(c, x + cwid - 36, y + ch - 19, on);

            if (inView) {
                hit(x, Math.max(y, top), x + cwid, Math.min(y + ch, bottom), mod::toggle);
            }
            if (mod.hasOptions()) {
                String lab = Lang.t("ui.settings");
                float lw = Draw.width(lab, 0.75f, true);
                boolean so = inView && over(x + 8, y + ch - 20, (int) (x + 12 + lw), y + ch - 4);
                Draw.text(c, lab, x + 10, y + ch - 16, so ? ACC : MUTED, 0.75f, true, false);
                if (inView) {
                    hit(x + 8, y + ch - 20, (int) (x + 12 + lw), y + ch - 4, () -> {
                        sel = mod;
                    });
                }
            }
        }

        // przykryj nadmiar przy przewijaniu
        c.fill(SIDE + 1, 37, W - 1, top - 2, PANEL);
        c.fill(SIDE + 1, bottom + 1, W - 1, H - 1, PANEL);

        Draw.text(c, cat.title(), cx, 45, TXT, 1.2f, true, false);
        int active = 0;
        for (Module mm : mods) if (mm.enabled) active++;
        Draw.text(c, active + "/" + mods.size() + " " + Lang.t("ui.active"), cx, 60, MUTED, 0.8f, false, false);
        c.fill(cx, 70, cx + 28, 72, ACC);
    }

    private void drawModuleSettings(DrawContext c, float dt, int cx, int cw) {
        Module mod = sel;
        float slide = (1f - Util.ease((selAnim - 0.5f) * 2f)) * 18f;
        int x0 = (int) (cx + slide);

        boolean bo = over(x0, 42, x0 + 60, 56);
        Draw.text(c, Lang.t("ui.back"), x0, 44, bo ? ACC : MUTED, 0.85f, true, false);
        hit(x0, 42, x0 + 60, 56, () -> sel = null);

        Draw.text(c, mod.name(), x0, 60, TXT, 1.2f, true, false);
        float on = an("on." + mod.id, mod.enabled ? 1f : 0f, dt, 12f);
        toggleSwitch(c, x0 + cw - 28, 62, on);
        hit(x0 + cw - 30, 58, x0 + cw, 78, mod::toggle);
        int y = 86;
        c.fill(x0, y - 4, x0 + cw, y - 3, LINE);

        for (Setting st : mod.settings) {
            Draw.text(c, st.label(), x0, y + 6, TXT, 0.9f, false, false);
            if (st.isBool) {
                float a = an("set." + mod.id + "." + st.key, st.flag ? 1f : 0f, dt, 12f);
                toggleSwitch(c, x0 + cw - 28, y + 4, a);
                hit(x0 + cw - 30, y, x0 + cw, y + 22, () -> {
                    st.flag = !st.flag;
                    Config.save();
                });
            } else {
                int rx = x0 + cw;
                boolean hp = over(rx - 16, y + 3, rx - 2, y + 17);
                boolean hm = over(rx - 74, y + 3, rx - 60, y + 17);
                Draw.box(c, rx - 16, y + 3, 14, 14, hp ? ACC : 0xFFF1F3F7, LINE);
                Draw.textCentered(c, "+", rx - 9, y + 5, hp ? 0xFFFFFFFF : TXT, 0.95f, true, false);
                Draw.box(c, rx - 74, y + 3, 14, 14, hm ? ACC : 0xFFF1F3F7, LINE);
                Draw.textCentered(c, "−", rx - 67, y + 5, hm ? 0xFFFFFFFF : TXT, 0.95f, true, false);
                Draw.textCentered(c, st.display(), rx - 38, y + 6, TXT, 0.9f, true, false);
                hit(rx - 16, y + 3, rx - 2, y + 17, () -> {
                    st.change(1);
                    Config.save();
                });
                hit(rx - 74, y + 3, rx - 60, y + 17, () -> {
                    st.change(-1);
                    Config.save();
                });
            }
            y += 24;
        }
        if (mod.hintKey != null) {
            Draw.text(c, Lang.t(mod.hintKey), x0, y + 4, MUTED, 0.8f, false, false);
            y += 20;
        }
        for (Module.Action a : mod.actions) {
            boolean ho = over(x0, y, x0 + cw, y + 24);
            float h = an("act." + a.key(), ho ? 1f : 0f, dt, 14f);
            Draw.box(c, x0, y, cw, 24, Util.lerpColor(0xFFF8F9FC, ACC, h), Util.lerpColor(LINE, 0xFFB5231F, h));
            Draw.textCentered(c, Lang.t(a.key()), x0 + cw / 2f, y + 8, Util.lerpColor(TXT, 0xFFFFFFFF, h), 0.95f, true, false);
            hit(x0, y, x0 + cw, y + 24, () -> {
                close();
                a.run().run();
            });
            y += 30;
        }
    }

    private void drawSettingsTab(DrawContext c, float dt, int cx, int cw) {
        Draw.text(c, Lang.t("ui.language"), cx, 45, TXT, 1.2f, true, false);
        c.fill(cx, 70, cx + 28, 72, ACC);

        int y = 80;
        for (int i = 0; i < 3; i++) {
            boolean ho = over(cx, y, cx + cw, y + 28);
            float h = an("lang.h" + i, ho ? 1f : 0f, dt, 14f);
            float on = an("lang.on" + i, Lang.current == i ? 1f : 0f, dt, 12f);
            int bg = Util.lerpColor(CARD, 0xFFFFFFFF, h);
            int border = Util.lerpColor(Util.lerpColor(LINE, ACC, on), ACC, h * 0.6f);
            Draw.box(c, cx, y, cw, 28, bg, border);
            Draw.text(c, Lang.NAMES[i], cx + 14, y + 9, TXT, 1.0f, Lang.current == i, false);
            // znacznik
            int kx = cx + cw - 22;
            Draw.box(c, kx, y + 8, 12, 12, Util.lerpColor(0xFFFFFFFF, ACC, on), Util.lerpColor(0xFFC3C9D4, 0xFFB5231F, on));
            final int idx = i;
            hit(cx, y, cx + cw, y + 28, () -> {
                Lang.current = idx;
                Config.save();
            });
            y += 36;
        }

        y += 4;
        c.fill(cx, y - 4, cx + cw, y - 3, LINE);
        String[] keys = {"ui.hud_mark", "ui.hud_list"};
        for (int i = 0; i < 2; i++) {
            boolean v = i == 0 ? Config.hudMark : Config.hudList;
            float a = an("hud" + i, v ? 1f : 0f, dt, 12f);
            Draw.text(c, Lang.t(keys[i]), cx, y + 6, TXT, 0.9f, false, false);
            toggleSwitch(c, cx + cw - 28, y + 4, a);
            final int idx = i;
            hit(cx, y, cx + cw, y + 22, () -> {
                if (idx == 0) Config.hudMark = !Config.hudMark;
                else Config.hudList = !Config.hudList;
                Config.save();
            });
            y += 24;
        }
        Draw.text(c, Lang.t("ui.about"), cx, H - 22, MUTED, 0.75f, false, false);
    }

    // ---------- input ----------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double mx = (mouseX - ox) / s, my = (mouseY - oy) / s;
            for (int i = hits.size() - 1; i >= 0; i--) {
                Hit h = hits.get(i);
                if (mx >= h.x1 && mx < h.x2 && my >= h.y1 && my < h.y2) {
                    h.run.run();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (tab < 3 && sel == null) {
            scrollT -= (float) vertical * 20f;
            if (scrollT < 0) scrollT = 0;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
