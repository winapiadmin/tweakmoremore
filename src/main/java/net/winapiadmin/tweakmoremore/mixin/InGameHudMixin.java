package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerInventory;
import net.winapiadmin.tweakmoremore.Main;
import net.winapiadmin.tweakmoremore.util.DeferredSlotHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSelectedSlot()I"))
    private int redirectGetSelectedSlot(PlayerInventory inventory) {
        if (Main.config.get("attribute_swap_fix.enabled", false)) {
            return DeferredSlotHelper.get(inventory);
        }
        return inventory.getSelectedSlot();
    }
}
