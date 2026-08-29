package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.winapiadmin.tweakmoremore.Main;
@Mixin(ServerPlayerEntity.class)
public abstract class DeathSaveMixin {

	@Shadow
	public MinecraftServer server;

	@Inject(method = "onDeath", at = @At("TAIL"))
	private void mixin$savePlayerDataOnDeath(CallbackInfo ci) {
                if (Main.config.get("bugfix.ServerPlayerEntity.savePlayerDataOnDeath",false))
		((PlayerManagerAccessor) this.server.getPlayerManager()).callSavePlayerData((ServerPlayerEntity)(Object) this);
	}
}