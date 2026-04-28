package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {
  @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  public void tick(CallbackInfo ci) {
    if (!Main.config.get("tickPlayer", true))
      ci.cancel();
  }
  @Redirect(method = "tick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/block/entity/"
                              + "SculkShriekerWarningManager;tick()V"))
  public void
  tickSculkShriekerWarningManager(SculkShriekerWarningManager instance) {
    if (!Main.config.get("tickSculkShriekerWarningManager", true))
      return;
    instance.tick();
  }
  @Redirect(method = "tick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/server/network/"
                              + "ServerPlayerEntity;tickFallStartPos()V"))
  public void
  tickFallStartPos(ServerPlayerEntity instance) {
    if (!Main.config.get("tickFallStartPos", true))
      return;
    instance.tickFallStartPos();
  }
  @Redirect(method = "tick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/server/network/"
                              +
                              "ServerPlayerEntity;tickVehicleInLavaRiding()V"))
  public void
  tickVehicleInLavaRiding(ServerPlayerEntity instance) {
    if (!Main.config.get("tickVehicleInLavaRiding", true))
      return;
    instance.tickVehicleInLavaRiding();
  }
}
