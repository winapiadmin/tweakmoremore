package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.block.ComposterBlock;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {
    @ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 20))
    public int modConst1(int i) {
        return Main.config.get("composterCompostDelay",20);
    }

    @ModifyConstant(method = "addToComposter", constant = @Constant(intValue = 20))
    private static int modConst2(int i) {
        return Main.config.get("composterCompostDelay",20);
    }
}