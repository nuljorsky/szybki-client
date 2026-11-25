package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Vec3d;
import pl.szybki.client.Category;
import pl.szybki.client.Lang;
import pl.szybki.client.Module;
import pl.szybki.client.util.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Gdy gracz stojacy PRZED toba zaklada elytre (lub zaczyna nia leciec),
 * automatycznie bierze wedke "Nielotek" do lewej reki.
 */
public class AutoNielot extends Module {
    private final Map<UUID, boolean[]> prev = new HashMap<>();
    private int cooldown = 0;

    public AutoNielot() {
        super("autonielot", Category.HELPERS);
        num("dist", 8, 3, 20, 1);
        flag("anyrod", true);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!enabled || mc.player == null || mc.world == null) {
            prev.clear();
            return;
        }
        if (cooldown > 0) cooldown--;
        Set<UUID> seen = new HashSet<>();
        for (AbstractClientPlayerEntity p : Util.others(mc)) {
            UUID id = p.getUuid();
            seen.add(id);
            boolean elytra = p.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
            boolean glide = Util.gliding(p);
            boolean[] old = prev.get(id);
            if (old != null && mc.player.distanceTo(p) <= d("dist") && inFront(mc, p)
                    && ((elytra && !old[0]) || (glide && !old[1]))) {
                trigger(mc);
            }
            prev.put(id, new boolean[]{elytra, glide});
        }
        prev.keySet().retainAll(seen);
    }

    private boolean inFront(MinecraftClient mc, AbstractClientPlayerEntity p) {
        Vec3d to = p.getPos().subtract(mc.player.getPos());
        if (to.lengthSquared() < 1e-6) return true;
        to = to.normalize();
        Vec3d look = mc.player.getRotationVec(1.0f);
        return to.x * look.x + to.z * look.z > 0.4 * Math.sqrt(look.x * look.x + look.z * look.z);
    }

    private boolean isNielot(ItemStack s) {
        return s.getName().getString().toLowerCase(Locale.ROOT).contains("nielot");
    }

    private void trigger(MinecraftClient mc) {
        if (cooldown > 0 || mc.interactionManager == null) return;
        ItemStack off = mc.player.getOffHandStack();
        if (off.isOf(Items.FISHING_ROD) && (isNielot(off) || b("anyrod"))) return;

        int best = -1, any = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (!s.isOf(Items.FISHING_ROD)) continue;
            if (isNielot(s)) {
                best = i;
                break;
            }
            if (any < 0) any = i;
        }
        if (best < 0 && b("anyrod")) best = any;
        if (best < 0) return;

        // SWAP z przyciskiem 40 = zamiana ze slotem lewej reki
        mc.interactionManager.clickSlot(0, Util.slotId(best), 40, SlotActionType.SWAP, mc.player);
        cooldown = 40;
        Util.msg(Lang.t("msg.nielot"));
    }
}
