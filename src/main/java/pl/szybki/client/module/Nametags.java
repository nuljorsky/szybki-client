package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import pl.szybki.client.Category;
import pl.szybki.client.Module;
import pl.szybki.client.ModuleManager;
import pl.szybki.client.util.Draw;
import pl.szybki.client.util.Projection;
import pl.szybki.client.util.Util;

/** Nametagi PRO: nick, HP, dystans, pasek zdrowia i ekwipunek (zbroja + rece). */
public class Nametags extends Module {
    public Nametags() {
        super("nametags", Category.RADAR);
        num("range", 150, 25, 300, 25);
        flag("items", true);
        flag("hidevanilla", true);
    }

    public static boolean hideVanilla() {
        Nametags n = ModuleManager.NAMETAGS;
        return n != null && n.enabled && n.b("hidevanilla");
    }

    @Override
    public void onHud(DrawContext c, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        double range = d("range");
        boolean items = b("items");
        double[] out = new double[3];
        for (AbstractClientPlayerEntity p : Util.others(mc)) {
            Vec3d pp = p.getLerpedPos(td);
            double dx = pp.x - Projection.cx, dy = pp.y - Projection.cy, dz = pp.z - Projection.cz;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > range) continue;
            if (!Projection.project(pp.x, pp.y + p.getHeight() + 0.4, pp.z, out)) continue;
            if (out[0] < -80 || out[0] > Projection.sw + 80 || out[1] < -80 || out[1] > Projection.sh + 80) continue;

            float sc = (float) Math.max(0.7, Math.min(1.15, 1.45 - dist / 70.0));
            String name = p.getName().getString();
            String dst = Math.round(dist) + "m";
            float hp = p.getHealth() + p.getAbsorptionAmount();
            float max = Math.max(1f, p.getMaxHealth());
            String hps = String.format(java.util.Locale.ROOT, "%.1f", hp);

            float wn = Draw.width(name, 0.9f, true);
            float wd = Draw.width("  " + dst, 0.8f, false);
            float wh = Draw.width("  " + hps, 0.8f, false);
            int w = (int) (wn + wd + wh) + 10;
            int hx = -w / 2;

            MatrixStack m = c.getMatrices();
            m.push();
            m.translate((float) out[0], (float) out[1], 0);
            m.scale(sc, sc, 1f);

            float ratio = Math.max(0f, Math.min(1f, hp / max));
            int hpCol = Util.lerpColor(0xFFFF3B30, 0xFF34D058, ratio);

            // pill z nickiem
            c.fill(hx, -14, hx + w, -2, 0xCC101419);
            c.fill(hx, -3, hx + (int) (w * ratio), -1, hpCol);
            c.fill(hx + (int) (w * ratio), -3, hx + w, -1, 0x66000000);
            float tx = hx + 5;
            tx += Draw.text(c, name, tx, -12f, 0xFFFFFFFF, 0.9f, true, false);
            tx += Draw.text(c, "  " + dst, tx, -11.5f, 0xFFB5BCC8, 0.8f, false, false);
            Draw.text(c, "  " + hps, tx, -11.5f, hpCol, 0.8f, true, false);

            if (items) {
                ItemStack[] st = {
                        p.getEquippedStack(EquipmentSlot.MAINHAND),
                        p.getEquippedStack(EquipmentSlot.OFFHAND),
                        p.getEquippedStack(EquipmentSlot.HEAD),
                        p.getEquippedStack(EquipmentSlot.CHEST),
                        p.getEquippedStack(EquipmentSlot.LEGS),
                        p.getEquippedStack(EquipmentSlot.FEET)
                };
                int n = 0;
                for (ItemStack s : st) if (!s.isEmpty()) n++;
                float step = 10f;
                float x0 = -n * step / 2f;
                int i = 0;
                for (ItemStack s : st) {
                    if (s.isEmpty()) continue;
                    m.push();
                    m.translate(x0 + i * step, -25f, 0);
                    m.scale(0.6f, 0.6f, 1f);
                    c.drawItem(s, 0, 0);
                    m.pop();
                    i++;
                }
            }
            m.pop();
        }
    }
}
