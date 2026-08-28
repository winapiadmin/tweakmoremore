package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.winapiadmin.tweakmoremore.Main;
import net.winapiadmin.tweakmoremore.util.DeferredSlotHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V", ordinal = 0))
    private void redirectSetSelectedSlot(PlayerInventory inventory, int slot) {
        if (Main.config.get("attribute_swap_fix.enabled", false)) {
            DeferredSlotHelper.defer(inventory, slot);
        } else {
            inventory.setSelectedSlot(slot);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setPhase(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
    private void beforeRenderPhase(boolean tick, CallbackInfo ci) {
        if (Main.config.get("attribute_swap_fix.enabled", false)) {
            MinecraftClient self = (MinecraftClient)(Object)this;
            if (self.player != null) {
                DeferredSlotHelper.apply(self.player.getInventory());
            }
        }
    }
}
