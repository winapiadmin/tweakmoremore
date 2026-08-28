package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.EntityHitResult;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public class SnowballEntityMixin {
    @Inject(method = "onEntityHit", at = @At("RETURN"))
    private void addPlayerDamage(EntityHitResult entityHitResult, CallbackInfo ci) {
        String id = Registries.ENTITY_TYPE.getId(entityHitResult.getEntity().getType()).toShortString();
        int damage = Main.config.get("snowball.damage_to_" + id, 0);
        if (damage > 0) {
            SnowballEntity self = (SnowballEntity)(Object)this;
            entityHitResult.getEntity().serverDamage(self.getDamageSources().thrown(self, self.getOwner()), damage);
        }
    }
}
