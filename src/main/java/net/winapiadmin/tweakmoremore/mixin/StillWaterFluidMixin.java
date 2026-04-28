package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WaterFluid.Still.class)
public class StillWaterFluidMixin {
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getLevel(FluidState state) {
    return Main.config.get("stillWaterFluidLevel", 8);
  }
}
