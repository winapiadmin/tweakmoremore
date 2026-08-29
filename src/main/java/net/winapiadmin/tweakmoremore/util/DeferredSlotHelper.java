package net.winapiadmin.tweakmoremore.util;

import java.util.WeakHashMap;
import net.minecraft.entity.player.PlayerInventory;

public class DeferredSlotHelper {
    private static final WeakHashMap<PlayerInventory, Integer> deferredSlots = new WeakHashMap<>();

    public static void defer(PlayerInventory inventory, int slot) { deferredSlots.put(inventory, slot); }

    public static int get(PlayerInventory inventory) { return deferredSlots.getOrDefault(inventory, inventory.getSelectedSlot()); }

    public static void apply(PlayerInventory inventory) {
        Integer deferred = deferredSlots.remove(inventory);
        if (deferred != null) {
            inventory.selectedSlot = deferred;
        }
    }
}
