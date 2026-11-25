package pl.szybki.client.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.szybki.client.module.Nametags;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    /** Gdy wlaczone Nametags PRO, chowamy domyslne nametagi (rysujemy wlasne). */
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true, require = 0)
    private void szybki$hideLabel(CallbackInfoReturnable<Boolean> cir) {
        if (Nametags.hideVanilla()) cir.setReturnValue(false);
    }
}
