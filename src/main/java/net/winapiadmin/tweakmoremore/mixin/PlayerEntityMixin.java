package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.winapiadmin.tweakmoremore.Main;
import net.minecraft.text.Text;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public int experienceLevel, totalExperience;
    @Shadow public float experienceProgress;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final String default_deathXPDropFormula = "min(experienceLevel*7,100)";

    @ModifyReturnValue(
        method = "getExperienceToDrop(Lnet/minecraft/server/world/ServerWorld;)I",
        at = @At("RETURN")
    )
    private int modifyXPDrop(int originalXP, ServerWorld world) {
        try {
            Expression expression = new ExpressionBuilder(
                    Main.config.get("player.xp.dropFormula", default_deathXPDropFormula))
                    .variable("experienceLevel")
                    .variable("totalExperience")
                    .variable("experienceProgress")
                    .build()
                    .setVariable("experienceLevel", experienceLevel)
                    .setVariable("totalExperience", totalExperience)
                    .setVariable("experienceProgress", experienceProgress);

            return (int) expression.evaluate();

        } catch (Exception e) {
            return originalXP;
        }
    }
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity)(Object)this;
        if (Main.config.get("entity.showFallDist", false) && self.isOnGround()) {
            self.sendMessage(Text.literal("fall distance: " + self.fallDistance), true);
        }
    }
}
