package net.winapiadmin.tweakmoremore.mixin;

import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.winapiadmin.tweakmoremore.EvalHelper;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("RETURN"))
    private void afterDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.config.get("player.showDamageInfo", false))
            return;

        boolean result = cir.getReturnValue();

        Entity attacker = source.getAttacker();
        Entity sourceEntity = source.getSource();
        Entity target = (Entity)(Object)this;

        Main.LOGGER.info("---- Damage Event ----");
        Main.LOGGER.info("Target: {}", target.getName().getString());
        Main.LOGGER.info("Amount: {}", amount);
        Main.LOGGER.info("Result (applied?): {}", result);
        Main.LOGGER.info("Damage Type: {}", source.getType().msgId());

        if (attacker != null) {
            Main.LOGGER.info("Attacker: {}", attacker.getName().getString());

            if (attacker instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal(target.getName().getString() + " got damaged by " + player.getName().getString() + " for " + amount + " damage (" + (result ? "applied" : "blocked") + ")"), false);
            }
        }

        if (sourceEntity != null) {
            Main.LOGGER.info("Source Entity: {}", sourceEntity.getName().getString());
        }

        Main.LOGGER.info("Bypasses Armor: {}", source.isIn(DamageTypeTags.BYPASSES_ARMOR));
        Main.LOGGER.info("Bypasses Invulnerability: {}", source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY));
    }

    @ModifyVariable(method = "damageEquipment", at = @At("STORE"), ordinal = 0)
    private int modifyDamageAmount(int original, DamageSource source, float amount, EquipmentSlot[] slots) {
        String key = "entity.damageEquipmentBehavior<" + source.getType().msgId() + ">.damageExpression";
        return EvalHelper.evaluateInt(key, Main.config.get(key, "max(1.0,amount/4.0)"), Map.of("n", (double)slots.length, "amount", (double)amount), Math.max(1, (int)(amount / 4.0f)));
    }
}
