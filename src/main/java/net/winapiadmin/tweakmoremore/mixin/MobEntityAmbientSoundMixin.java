package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityAmbientSoundMixin {

	@Shadow
	public int ambientSoundChance;

	@Shadow
	public int getMinAmbientSoundDelay() {
		return 80;
	}

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void onBaseTickTail(CallbackInfo ci) {
                if (!Main.config.get("bugfix.MobEntity.clampAmbientSoundChance", false)) return;
		int minDelay = this.getMinAmbientSoundDelay();
		if (minDelay > 0 && this.ambientSoundChance < -minDelay) {
			this.ambientSoundChance = -minDelay;
		}
	}
}
