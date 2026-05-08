package net.winapiadmin.tweakmoremore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.winapiadmin.tweakmoremore.Main;
@Mixin(Inventory.class)
public interface InventoryMixin {
    @ModifyReturnValue
            (
                    method = "getMaxCountPerStack()I",
                    at = @At("RETURN")
            )
    default int getMaxCountPerStack(int constant)
    {
        return Main.config.get("item.<any>.maxCountPerStack", constant);
    }
}
