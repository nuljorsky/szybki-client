package pl.szybki.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import pl.szybki.client.util.Draw;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Jasne, animowane tlo z latajacymi minecraftowymi mieczami (glownie czarnymi, troche jasnych). */
public final class SwordField {
    private static final class S {
        float x, y, vx, vy, rot, vr, size;
        boolean dark;
        int alpha;
    }

    private final List<S> swords = new ArrayList<>();
    private final Random rnd = new Random(1406);
    private long last = System.nanoTime();
    private float time = 0f;
    private int lw = -1, lh = -1;

    private void spawn(int w, int h) {
        swords.clear();
        int n = 30;
        for (int i = 0; i < n; i++) {
            S s = new S();
            s.size = 1.2f + rnd.nextFloat() * 3.6f;
            s.x = rnd.nextFloat() * w;
            s.y = rnd.nextFloat() * h;
            float spd = 6f + s.size * 5f;
            double ang = Math.toRadians(-35 + rnd.nextFloat() * 20);
            s.vx = (float) (Math.cos(ang) * spd);
            s.vy = (float) (Math.sin(ang) * spd);
            s.rot = rnd.nextFloat() * 360f;
            s.vr = (rnd.nextFloat() - 0.5f) * 70f;
            s.dark = rnd.nextFloat() < 0.72f;
            s.alpha = (int) Math.min(210, 60 + s.size * 32);
            if (!s.dark) s.alpha = (int) (s.alpha * 0.9f);
            swords.add(s);
        }
    }

    public void render(DrawContext c, int w, int h) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - last) / 1e9f);
        last = now;
        time += dt;
        if (w != lw || h != lh) {
            lw = w;
            lh = h;
            spawn(w, h);
        }

        // jasny gradient
        c.fillGradient(0, 0, w, h, 0xFFF9FBFE, 0xFFD6DEEA);

        // przesuwajace sie jasne smugi
        MatrixStack m = c.getMatrices();
        for (int i = 0; i < 4; i++) {
            float off = ((time * (14f + i * 6f) + i * 170f) % (w + 400f)) - 200f;
            m.push();
            m.translate(off, h / 2f, 0);
            m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(24f));
            c.fill(-18 - i * 6, -h, 18 + i * 6, h, 0x14FFFFFF);
            c.fill(-4, -h, 4, h, 0x10E5322D);
            m.pop();
        }

        // miecze (od najmniejszych = daleko, do najwiekszych = blisko)
        swords.sort((a, b) -> Float.compare(a.size, b.size));
        for (S s : swords) {
            s.x += s.vx * dt;
            s.y += s.vy * dt;
            s.rot += s.vr * dt;
            float margin = 70f;
            if (s.x > w + margin) s.x = -margin;
            if (s.x < -margin) s.x = w + margin;
            if (s.y > h + margin) s.y = -margin;
            if (s.y < -margin) s.y = h + margin;
            float bob = (float) Math.sin(time * 0.9 + s.size * 3) * 3f;
            Draw.sword(c, s.x, s.y + bob, s.size, s.rot, s.dark, s.alpha);
        }
    }
}
