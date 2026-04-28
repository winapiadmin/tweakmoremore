// credit to stackable127
package net.winapiadmin.tweakmoremore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public interface InventoryMixin {
    @ModifyReturnValue
            (
                    method = "getMaxCountPerStack()I",
                    at = @At("RETURN")
            )
    default int getMaxCountPerStack(int constant)
    {
        if (constant != 99)
        {
            return constant;
        }
        return Integer.MAX_VALUE; //We ignore the original, we could do a check to ensure it was 64, however, this should always be 64, this is the base case.
    }
}
