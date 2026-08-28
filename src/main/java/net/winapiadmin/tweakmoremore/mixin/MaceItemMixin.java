package net.winapiadmin.tweakmoremore.mixin;

import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MaceItem.class)
public class MaceItemMixin {
    /**
     * @author winapiadmin
     * @reason Configurable minimum smash height and optional gliding allowance.
     */
    @Overwrite
    public static boolean shouldDealAdditionalDamage(LivingEntity attacker) {
        boolean requireNotGliding = (boolean)Main.config.get("maceSmashRequiresNotGliding", true);
        double minHeight = (double)Main.config.get("maceSmashMinFallDistance", 1.5D);

        return attacker.fallDistance > minHeight && (!requireNotGliding || !attacker.isGliding());
    }

    /**
     * @author winapiadmin
     * @reason Configurable smash damage curve.
     */
    @Overwrite
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        if (!(damageSource.getSource() instanceof LivingEntity livingEntity)) {
            return 0.0F;
        } else if (!MaceItem.shouldDealAdditionalDamage(livingEntity)) {
            return 0.0F;
        } else {
            double lowThreshold = (double)Main.config.get("maceSmashFallThresholdLow", 3.0D);
            double highThreshold = (double)Main.config.get("maceSmashFallThresholdHigh", 8.0D);
            double lowRate = (double)Main.config.get("maceSmashDamagePerBlockLow", 4.0D);
            double midRate = (double)Main.config.get("maceSmashDamagePerBlockMid", 2.0D);
            double highRate = (double)Main.config.get("maceSmashDamagePerBlockHigh", 1.0D);
            double fallDistance = livingEntity.fallDistance;
            double g;
            if (fallDistance <= lowThreshold) {
                g = lowRate * fallDistance;
            } else if (fallDistance <= highThreshold) {
                g = lowRate * lowThreshold + midRate * (fallDistance - lowThreshold);
            } else {
                g = lowRate * lowThreshold + midRate * (highThreshold - lowThreshold) + highRate * (fallDistance - highThreshold);
            }

            return livingEntity.getEntityWorld() instanceof ServerWorld serverWorld
                ? (float)(g + EnchantmentHelper.getSmashDamagePerFallenBlock(serverWorld, livingEntity.getWeaponStack(), target, damageSource, 0.0F) * fallDistance)
                : (float)g;
        }
    }

    /**
     * @author winapiadmin
     * @reason Configurable smash knockback range and power.
     */
    @Overwrite
    private static void knockbackNearbyEntities(World world, Entity attacker, Entity attacked) {
        double power = (double)Main.config.get("maceSmashKnockbackPower", 0.7D);
        world.syncWorldEvent(WorldEvents.SMASH_ATTACK, attacked.getSteppingPos(), 750);
        world.getEntitiesByClass(LivingEntity.class, attacked.getBoundingBox().expand((double)Main.config.get("maceSmashKnockbackRange", 3.5D)), getKnockbackPredicate(attacker, attacked)).forEach(entity -> {
            Vec3d vec3d = entity.getEntityPos().subtract(attacked.getEntityPos());
            double d = getKnockback(attacker, entity, vec3d);
            Vec3d vec3d2 = vec3d.normalize().multiply(d);
            if (d > 0.0) {
                entity.addVelocity(vec3d2.x, power, vec3d2.z);
                if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                    serverPlayerEntity.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayerEntity));
                }
            }
        });
    }

    /**
     * @author winapiadmin
     * @reason Configurable smash knockback selection radius.
     */
    @Overwrite
    private static Predicate<LivingEntity> getKnockbackPredicate(Entity attacker, Entity attacked) {
        double range = (double)Main.config.get("maceSmashKnockbackRange", 3.5D);
        return entity -> {
            boolean nonSpectating = !entity.isSpectator();
            boolean nonSelfAttacking = entity != attacker && entity != attacked;
            boolean notTeammate = !attacker.isTeammate(entity);
            boolean notTamed = !(
                entity instanceof TameableEntity tameableEntity
                    && attacked instanceof LivingEntity livingEntity
                    && tameableEntity.isTamed()
                    && tameableEntity.isOwner(livingEntity)
            );
            boolean notArmorStand = !(entity instanceof ArmorStandEntity armorStandEntity && armorStandEntity.isMarker());
            boolean bl6 = attacked.squaredDistanceTo(entity) <= Math.pow(range, 2.0D);
            boolean nonCreative = !(entity instanceof PlayerEntity playerEntity && playerEntity.isCreative() && playerEntity.getAbilities().flying);
            return nonSpectating && nonSelfAttacking && notTeammate && notTamed && notArmorStand && bl6 && nonCreative;
        };
    }

    /**
     * @author winapiadmin
     * @reason Configurable smash knockback strength.
     */
    @Overwrite
    private static double getKnockback(Entity attacker, LivingEntity attacked, Vec3d distance) {
        double range = (double)Main.config.get("maceSmashKnockbackRange", 3.5D);
        double power = (double)Main.config.get("maceSmashKnockbackPower", 0.7D);
        double heavyMultiplier = (double)Main.config.get("maceSmashHeavyKnockbackMultiplier", 2.0D);
        return (range - distance.length()) * power * (attacker.fallDistance > 5.0F ? heavyMultiplier : 1.0D) * (1.0D - attacked.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
    }
}
