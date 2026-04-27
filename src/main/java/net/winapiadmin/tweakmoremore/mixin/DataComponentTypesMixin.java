package net.winapiadmin.tweakmoremore.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.winapiadmin.tweakmoremore.Main;
@Mixin(DataComponentTypes.class)
public class DataComponentTypesMixin {

    @ModifyExpressionValue
            (
                    method = "<clinit>",
                    at = @At(value = "CONSTANT", args = "intValue=64")
            )
    private static int getMaxCountPerStack(int original) {
        return Main.config.get("item.codec.maxStackSize", original);
    }
}