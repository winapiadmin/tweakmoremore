package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @ModifyConstant(
        method = "tick",
        constant = @Constant(intValue = 400)
    )
    private static int tweakBrewTime(int original) {
        return Main.config.get("brewing_stands.brewTime", original);
    }
    @ModifyConstant(
        method = "tick",
        constant = @Constant(intValue = 20)
    )
    private static int tweakBrewFuel(int original) {
        return Main.config.get("brewing_stands.brewFuel", original);
    }
    @Inject(method="tick", at=@At("HEAD"), cancellable=true)
    private static void tick(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci){
        if (!(boolean)Main.config.get("brewing_stands.tick", true))
          ci.cancel();
    }
}