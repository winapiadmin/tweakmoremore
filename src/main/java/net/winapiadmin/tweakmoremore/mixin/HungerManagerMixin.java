package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.player.HungerManager;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @ModifyConstant(method = "update", constant = @Constant(intValue = 10))
    private int modifyFastRegenInterval(int original) {
        return Main.config.get("regen.fast_interval", 10);
    }

    @ModifyConstant(method = "update", constant = @Constant(intValue = 80, ordinal = 0))
    private int modifySlowRegenInterval(int original) {
        return Main.config.get("regen.slow_interval", 80);
    }

    @ModifyConstant(method = "update", constant = @Constant(intValue = 80, ordinal = 1))
    private int modifyStarvationInterval(int original) {
        return Main.config.get("regen.starvation_interval", 80);
    }
}
