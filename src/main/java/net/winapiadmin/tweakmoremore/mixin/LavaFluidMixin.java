package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.winapiadmin.tweakmoremore.Main;
import net.winapiadmin.tweakmoremore.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getTickRate(WorldView world) {
    return LavaFluid.shouldLavaFlowFaster(world)
        ? Main.config.get("lavaFastPlayTickRate", 10)
        : Main.config.get("lavaNonFastPlayTickRate", 30);
  }
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getLevelDecreasePerBlock(WorldView world) {
    return LavaFluid.shouldLavaFlowFaster(world)
        ? Main.config.get("lavaFastPlayLevelDecreasePerBlock", 1)
        : Main.config.get("lavaNonFastPlayLevelDecreasePerBlock", 2);
  }
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getMaxFlowDistance(WorldView world) {
    return LavaFluid.shouldLavaFlowFaster(world)
        ? Main.config.get("lavaFastPlayMaxFlowDist", 4)
        : Main.config.get("lavaNonFastPlayMaxFlowDist", 2);
  }
  @Inject(method = "onRandomTick", at = @At("HEAD"), cancellable = true)
  public void onRandomTick(ServerWorld world, BlockPos pos, FluidState state,
                           Random random, CallbackInfo ci) {
    if (!(boolean)Main.config.get("lavaRandomTick", true))
      ci.cancel();
  }
}
