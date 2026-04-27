package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.village.raid.RaidManager;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(RaidManager.class)
public class RaidManagerMixin {
  @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  public void tick(CallbackInfo ci) {
    if (!(boolean)Main.config.get("tick_raids", true))
      ci.cancel();
  }
}