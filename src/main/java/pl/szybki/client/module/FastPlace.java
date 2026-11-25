package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import pl.szybki.client.Category;
import pl.szybki.client.Module;
import pl.szybki.client.mixin.MinecraftClientAccessor;

public class FastPlace extends Module {
    public FastPlace() {
        super("fastplace", Category.CHEATS);
        num("delay", 0, 0, 4, 1);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!enabled || mc.player == null) return;
        MinecraftClientAccessor acc = (MinecraftClientAccessor) mc;
        int max = (int) d("delay");
        if (acc.szybki$getItemUseCooldown() > max) acc.szybki$setItemUseCooldown(max);
    }
}
