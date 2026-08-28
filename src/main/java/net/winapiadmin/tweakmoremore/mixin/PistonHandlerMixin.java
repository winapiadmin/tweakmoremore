package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.block.piston.PistonHandler;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PistonHandler.class)
public class PistonHandlerMixin {
    @ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
    private int overridePushLimit(int original) {
        return Math.max(1, Main.config.get("piston.push_limit", original));
    }
}
