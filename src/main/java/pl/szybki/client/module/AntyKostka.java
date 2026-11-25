package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import pl.szybki.client.Category;
import pl.szybki.client.Lang;
import pl.szybki.client.Module;
import pl.szybki.client.util.Util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Zapisuje uklad ekwipunku (36 slotow) i potrafi go przywrocic, gdy sie pomiesza. */
public class AntyKostka extends Module {
    private final ItemStack[] saved = new ItemStack[36];
    private boolean hasSave = false;
    private final ArrayDeque<Integer> queue = new ArrayDeque<>();

    public AntyKostka() {
        super("antykostka", Category.HELPERS);
        actions.add(new Action("btn.save", this::save));
        actions.add(new Action("btn.restore", this::restore));
    }

    public void save() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        for (int i = 0; i < 36; i++) saved[i] = mc.player.getInventory().getStack(i).copy();
        hasSave = true;
        Util.msg(Lang.t("msg.saved"));
    }

    public void restore() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (!hasSave) {
            Util.msg(Lang.t("msg.nosave"));
            return;
        }
        // symulacja ukladu, zeby zaplanowac minimalna liste zamian
        ItemStack[] cur = new ItemStack[36];
        for (int i = 0; i < 36; i++) cur[i] = mc.player.getInventory().getStack(i).copy();
        boolean[] fixed = new boolean[36];
        List<Integer> clicks = new ArrayList<>();

        for (int i = 0; i < 36; i++) {
            ItemStack want = saved[i];
            if (want.isEmpty()) continue;
            if (!cur[i].isEmpty() && ItemStack.areItemsAndComponentsEqual(cur[i], want)) {
                fixed[i] = true;
                continue;
            }
            int j = -1;
            for (int k = 0; k < 36; k++) {
                if (k == i || fixed[k] || cur[k].isEmpty()) continue;
                if (ItemStack.areItemsAndComponentsEqual(cur[k], want)) {
                    j = k;
                    break;
                }
            }
            if (j < 0) continue;
            if (!cur[i].isEmpty()) {
                clicks.add(Util.slotId(i));
                clicks.add(Util.slotId(j));
                clicks.add(Util.slotId(i));
                ItemStack tmp = cur[i];
                cur[i] = cur[j];
                cur[j] = tmp;
            } else {
                clicks.add(Util.slotId(j));
                clicks.add(Util.slotId(i));
                cur[i] = cur[j];
                cur[j] = ItemStack.EMPTY;
            }
            fixed[i] = true;
        }
        queue.clear();
        queue.addAll(clicks);
        Util.msg(Lang.t("msg.restoring"));
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (queue.isEmpty()) return;
        if (mc.player == null || mc.interactionManager == null) {
            queue.clear();
            return;
        }
        // jesli otwarty jest inny kontener, wstrzymaj
        if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) return;
        for (int i = 0; i < 4 && !queue.isEmpty(); i++) {
            int slot = queue.poll();
            mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.PICKUP, mc.player);
        }
    }
}
