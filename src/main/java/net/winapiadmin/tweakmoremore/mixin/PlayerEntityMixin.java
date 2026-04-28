package net.winapiadmin.tweakmoremore.mixin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.winapiadmin.tweakmoremore.Main;
import net.minecraft.text.Text;

import java.util.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public int experienceLevel, totalExperience;
    @Shadow public float experienceProgress;
    @Shadow protected abstract void vanishCursedItems();
    @Shadow @Final PlayerInventory inventory;
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void sendMessage(Text par1, boolean par2);

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
        if (Main.config.get("entity.showFallDist", false) && ((PlayerEntity)(Object)this).fallDistance!=0.0F) {
            ((PlayerEntity)(Object)this).sendMessage(Text.literal("fall distance: " + ((PlayerEntity)(Object)this).fallDistance), true);
        }
    }
}
