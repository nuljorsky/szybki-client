package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import pl.szybki.client.Category;
import pl.szybki.client.Module;

import java.util.Optional;

/**
 * Bicie graczy przez sciany. Dziala tylko po kliknieciu LPM (nie automatycznie):
 * gdy vanilla nie celuje w encje, a w linii wzroku (w zasiegu) jest hitbox gracza - uderzamy go.
 */
public class Hitboxes extends Module {
    private boolean wasDown = false;

    public Hitboxes() {
        super("hitboxes", Category.CHEATS);
        num("reach", 3.0, 3.0, 6.0, 0.5);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!enabled || mc.player == null || mc.world == null || mc.interactionManager == null) {
            wasDown = false;
            return;
        }
        if (mc.currentScreen != null) {
            wasDown = false;
            return;
        }
        boolean down = mc.options.attackKey.isPressed();
        if (down && !wasDown) {
            boolean vanillaEntity = mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY;
            if (!vanillaEntity) {
                PlayerEntity target = find(mc);
                if (target != null) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                    // zuzyj klikniecie, zeby gra nie zaczela kopac bloku ze sciany
                    while (mc.options.attackKey.wasPressed()) { /* drain */ }
                    mc.options.attackKey.setPressed(false);
                }
            }
        }
        wasDown = down;
    }

    private PlayerEntity find(MinecraftClient mc) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d look = mc.player.getRotationVec(1.0f);
        double reach = d("reach");
        Vec3d end = eye.add(look.multiply(reach));
        PlayerEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator() || !p.isAlive()) continue;
            Box box = p.getBoundingBox().expand(p.getTargetingMargin());
            Optional<Vec3d> hit = box.raycast(eye, end);
            if (hit.isPresent()) {
                double dd = eye.squaredDistanceTo(hit.get());
                if (dd < bestD) {
                    bestD = dd;
                    best = p;
                }
            }
        }
        return best;
    }
}
