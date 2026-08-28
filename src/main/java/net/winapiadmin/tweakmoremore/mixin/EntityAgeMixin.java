package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityAgeMixin {

	@Shadow
	public int age;

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void onBaseTick(CallbackInfo ci) {
		if (Main.config.get("bugfix.Entity.clampAge", false) && this.age < 0) {
			this.age = 0;
		}
	}
}
