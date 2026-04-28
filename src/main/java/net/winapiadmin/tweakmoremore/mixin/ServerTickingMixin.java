package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SleepManager;
import net.minecraft.world.border.WorldBorder;
import net.winapiadmin.tweakmoremore.Main;
import net.winapiadmin.tweakmoremore.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerTickingMixin {
  @Inject(method = "tickWeather", at = @At("HEAD"), cancellable = true)
  private void tickWeather(CallbackInfo ci) {
    if (!(boolean)Main.config.get("tickWeather", true))
      ci.cancel();
  }
  @Inject(method = "tickTime", at = @At("HEAD"), cancellable = true)
  public void tickTime(CallbackInfo ci) {
    if (!(boolean)Main.config.get("tickTime", true))
      ci.cancel();
  }
  @Redirect(
      method = "tick",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/server/world/SleepManager;canSkipNight(I)Z"))
  private boolean
  canSkipNight(SleepManager sleepManager, int percentage) {
    if (!(boolean)Main.config.get("sleep_never_skip", true)) {
      return false;
    }

    return sleepManager.canSkipNight(percentage);
  }
  @Redirect(
      method = "tick",
      at = @At(
          value = "INVOKE",
          target =
              "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
  public void
  tickBlockEntities(ServerWorld instance) {
    if (!(boolean)Main.config.get("tickBlockEntities", true)) {
      return;
    }
    instance.tickBlockEntities();
  }

  @Redirect(
      method = "tick",
      at = @At(value = "INVOKE",
               target = "Lnet/minecraft/world/border/WorldBorder;tick()V"))
  private void
  tickWorldBorder(WorldBorder worldBorder) {
    if (!(boolean)Main.config.get("tickWorldBorder", true)) {
      return;
    }
    worldBorder.tick();
  }
}
