package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(AbstractMinecartEntity.class)
public abstract class MinecartCollisionMixin extends Entity {

	protected MinecartCollisionMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "pushAwayFromMinecart", at = @At("HEAD"), cancellable = true)
	private void mixin$onPushAwayFromMinecart(AbstractMinecartEntity entity, double xDiff, double zDiff, CallbackInfo ci) {
                if (Main.config.get("bugfix.minecart.correctPushAwayFromMinecart",false)) return;
		double d;
		double e;
		if (AbstractMinecartEntity.areMinecartImprovementsEnabled(this.getEntityWorld())) {
			d = this.getVelocity().x;
			e = this.getVelocity().z;
		} else {
			d = entity.getX() - this.getX();
			e = entity.getZ() - this.getZ();
		}

		Vec3d vec3d = new Vec3d(d, 0.0, e).normalize();
		Vec3d vec3d2 = new Vec3d(MathHelper.cos(this.getYaw() * (float)(Math.PI / 180.0)), 0.0, MathHelper.sin(this.getYaw() * (float)(Math.PI / 180.0)))
			.normalize();
		double f = Math.abs(vec3d.dotProduct(vec3d2));
		if (f < 0.8F && !AbstractMinecartEntity.areMinecartImprovementsEnabled(this.getEntityWorld())) {
			ci.cancel();
			return;
		}

		double g = (entity.getVelocity().x + this.getVelocity().x) / 2.0;
		double h = (entity.getVelocity().z + this.getVelocity().z) / 2.0;
		this.setVelocity(new Vec3d(g - xDiff, this.getVelocity().y, h - zDiff));
		entity.setVelocity(new Vec3d(g + xDiff, entity.getVelocity().y, h + zDiff));
		ci.cancel();
	}
}
