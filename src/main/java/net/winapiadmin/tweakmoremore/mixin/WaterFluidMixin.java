package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.world.WorldView;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {
  /**
   * @author winapiadmin
   * @reason modifies tick rate
   */
  @Overwrite
  public int getTickRate(WorldView world) {
    return Main.config.get("waterTickRate", 5);
  }
  /**
   * @author winapiadmin
   * @reason level decrease/block
   */
  @Overwrite
  public int getLevelDecreasePerBlock(WorldView world) {
    return Main.config.get("waterLevelDecreasePerBlock", 1);
  }
  /**
   * @author winapiadmin
   * @reason max flow dist
   */
  @Overwrite
  public int getMaxFlowDistance(WorldView world) {
    return Main.config.get("waterMaxFlowDist", 4);
  }
}