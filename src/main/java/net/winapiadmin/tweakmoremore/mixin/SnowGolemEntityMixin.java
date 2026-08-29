package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.passive.SnowGolemEntity;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowGolemEntity.class)
public class SnowGolemEntityMixin {
    @ModifyConstant(method = "initGoals", constant = @Constant(intValue = 20))
    private int modifyShootCooldown(int original) {
        return Main.config.get("snow_golem.shoot_cooldown", original);
    }

    @ModifyConstant(method = "initGoals", constant = @Constant(floatValue = 10.0F))
    private float modifyShootRange(float original) {
        return (float)Main.config.get("snow_golem.shoot_range", original);
    }

    @Inject(method = "hurtByWater", at = @At("RETURN"), cancellable = true)
    private void modifyHurtByWater(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Main.config.get("snow_golem.hurt_by_water", true));
    }
}
