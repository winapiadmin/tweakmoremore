package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.world.WorldView;
import net.winapiadmin.tweakmoremore.Main;
import net.winapiadmin.tweakmoremore.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getTickRate(WorldView world) {
    return Main.config.get("waterTickRate", 5);
  }
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getLevelDecreasePerBlock(WorldView world) {
    return Main.config.get("waterLevelDecreasePerBlock", 1);
  }
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getMaxFlowDistance(WorldView world) {
    return Main.config.get("waterMaxFlowDist", 4);
  }
}
