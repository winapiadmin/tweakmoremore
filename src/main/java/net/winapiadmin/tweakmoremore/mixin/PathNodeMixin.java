package net.winapiadmin.tweakmoremore.mixin;

import net.winapiadmin.tweakmoremore.Main;
import net.minecraft.entity.ai.pathing.PathNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathNode.class)
public class PathNodeMixin {

@Inject(method = "hash", at = @At("HEAD"), cancellable = true)
private static void hash(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
    if (!Main.config.get("bugfix.PathNode.properHash", false)) {
        return; // let vanilla run
    }

    int h = 1664525 * x + 1013904223;
    h = 1664525 * h + 1013904223 + y;
    h = 1664525 * h + 1013904223 + z;
    cir.setReturnValue(h);
}
}
