package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.winapiadmin.tweakmoremore.Main;
import net.winapiadmin.tweakmoremore.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LavaFluid.Still.class)
public class StillLavaFluidMixin {
  /**
   * @author
   * @reason
   */
  @Overwrite
  public int getLevel(FluidState state) {
    return Main.config.get("stillLavaFluidLevel", 8);
  }
}
