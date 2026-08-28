package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPos.class)
public class BlockPosMixin {

@Inject(
    method = "Lnet/minecraft/util/math/BlockPos;iterate(IIIIII)Ljava/lang/Iterable;",
    at = @At("HEAD"),
    cancellable = true
)
private static void iterate(
    int startX,
    int startY,
    int startZ,
    int endX,
    int endY,
    int endZ,
    CallbackInfoReturnable<Iterable<BlockPos>> cir
) {
    if (!Main.config.get("bugfix.BlockPos.enableOptimizedIterate", false)) {
        return;
    }

    long i = (long) endX - startX + 1L;
    long j = (long) endY - startY + 1L;
    long k = (long) endZ - startZ + 1L;
    long l = i * j * k;

    int ix = (int) i;
    int jx = (int) j;

    cir.setReturnValue(() -> new AbstractIterator<BlockPos>() {
        private final BlockPos.Mutable pos = new BlockPos.Mutable();
        private long index;

        @Override
        protected BlockPos computeNext() {
            if (index >= l) {
                return endOfData();
            }

            int iz = (int) (index % ix);
            int jz = (int) (index / ix);
            int kz = jz % jx;
            int lz = jz / jx;
            index++;

            return pos.set(
                startX + iz,
                startY + kz,
                startZ + lz
            );
        }
    });
}
}
