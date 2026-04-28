package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.block.SculkBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SculkBlock.class)
public abstract class SculkBlockMixin {

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/ExperienceDroppingBlock;<init>(Lnet/minecraft/util/math/intprovider/IntProvider;Lnet/minecraft/block/AbstractBlock$Settings;)V"
            ),
            index = 0
    )
    private static IntProvider modifyXpProvider(IntProvider original) {
        return UniformIntProvider.create(
                Main.config.get("sculk_minExp", original.getMin()),
                Main.config.get("sculk_maxExp", original.getMax())
        );
    }
}