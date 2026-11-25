package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import pl.szybki.client.Category;
import pl.szybki.client.Lang;
import pl.szybki.client.Module;
import pl.szybki.client.util.Util;

/** Wychodzi z serwera, gdy HP spadnie ponizej progu (lub gracz jest zbyt blisko - opcjonalnie). */
public class AutoLeave extends Module {
    public AutoLeave() {
        super("autoleave", Category.HELPERS);
        num("hp", 6, 1, 19, 1);
        num("near", 0, 0, 30, 5);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!enabled || mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return;
        boolean leave = mc.player.getHealth() + mc.player.getAbsorptionAmount() <= d("hp");
        double near = d("near");
        if (!leave && near > 0) {
            for (AbstractClientPlayerEntity p : Util.others(mc)) {
                if (mc.player.distanceTo(p) <= near) {
                    leave = true;
                    break;
                }
            }
        }
        if (leave) {
            // wylacz, zeby po ponownym wejsciu nie wylatywac w petli
            setEnabled(false);
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("[SZYBKI] " + Lang.t("msg.leave")));
        }
    }
}
