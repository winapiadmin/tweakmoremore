package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(TntMinecartEntity.class)
public abstract class TntMinecartCollisionMixin extends AbstractMinecartEntity {

	protected TntMinecartCollisionMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/vehicle/TntMinecartEntity;explode(Lnet/minecraft/entity/damage/DamageSource;D)V",
			ordinal = 1,
			shift = At.Shift.BEFORE
		),
		cancellable = true
	)
	private void mixin$skipRailDetonation(CallbackInfo ci) {
		if (Main.config.get("bugfix.tnt_minecart.skipRailDetonation",false) && this.isOnRail()) {
			ci.cancel();
		}
	}
}
