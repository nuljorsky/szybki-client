package pl.szybki.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.szybki.client.module.Freecam;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /** W freecam ruch myszka obraca kamere, a nie postac. */
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void szybki$look(double dx, double dy, CallbackInfo ci) {
        if (Freecam.active() && (Object) this == MinecraftClient.getInstance().player) {
            Freecam.I.look(dx, dy);
            ci.cancel();
        }
    }
}
