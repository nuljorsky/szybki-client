package pl.szybki.client.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.szybki.client.module.Freecam;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    /** W freecam kamera ma sie zachowywac jak "trzecia osoba", zeby bylo widac wlasne cialo. */
    @ModifyVariable(method = "update", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean szybki$thirdPerson(boolean thirdPerson) {
        return Freecam.active() || thirdPerson;
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void szybki$freecam(BlockView area, Entity focusedEntity, boolean tp, boolean inverseView, float tickDelta, CallbackInfo ci) {
        Freecam f = Freecam.I;
        if (f == null || !f.enabled) return;
        double x = f.px + (f.x - f.px) * tickDelta;
        double y = f.py + (f.y - f.py) * tickDelta;
        double z = f.pz + (f.z - f.pz) * tickDelta;
        this.setPos(x, y, z);
        this.setRotation(f.yaw, f.pitch);
    }
}
