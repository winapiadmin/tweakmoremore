package net.winapiadmin.tweakmoremore.mixin;

import net.winapiadmin.tweakmoremore.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityPistonVelocityMixin {

	@Unique
	private boolean mixin$isPistonMove;

	@Inject(method = "move", at = @At("HEAD"))
	private void onMoveHead(MovementType type, Vec3d movement, CallbackInfo ci) {
                if (Main.config.get("bugfix.Entity.correctPistonMovement", false))
    		    this.mixin$isPistonMove = type == MovementType.PISTON;
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
	private void redirectSetVelocityAtZero(Entity instance, Vec3d velocity) {
		if (!Main.config.get("bugfix.Entity.correctPistonVelocity", false) || !this.mixin$isPistonMove) {
			instance.setVelocity(velocity);
		}
	}
}
