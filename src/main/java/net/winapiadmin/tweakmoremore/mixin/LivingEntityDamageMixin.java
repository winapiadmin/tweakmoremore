package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.LivingEntity;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntityDamageMixin {
    @ModifyConstant(method = "damage", constant = @Constant(intValue = 20))
    private int modifyTimeUntilRegen(int original) {
        return Main.config.get("damage.invulnerability_ticks", original);
    }

    @ModifyConstant(method = "damage", constant = @Constant(intValue = 10))
    private int modifyMaxHurtTime(int original) {
        return Main.config.get("damage.hurt_animation_ticks", original);
    }
}
