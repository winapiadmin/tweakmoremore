// credit to stackable127
package net.winapiadmin.tweakmoremore.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.winapiadmin.tweakmoremore.Main;
@Mixin(ItemStack.class)
class ItemStackMixin {
    //1.2.2 - This is a lot cleaner and less crash prone. This was done to fix an issue with kubeJS, it might be worth making a PR for them since this should achieve the same effect and prevent other mods from crashing.
    @ModifyExpressionValue
            (
                    method = "method_57371", //This method is a Lambda, they aren't funda.
                    at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;")
            )
    private static Codec<Integer> replaceCodec(Codec<Integer> original)
    {
        return Codecs.rangedInt(0, Main.config.get("item.codec.maxStackSize", 64));
    }
}