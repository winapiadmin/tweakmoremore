package net.winapiadmin.tweakmoremore.mixin;

import java.util.function.Function;
import com.mojang.serialization.MapCodec;
import net.minecraft.component.ComponentChanges;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.component.ComponentChanges;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.winapiadmin.tweakmoremore.Main;
@Mixin(ItemStack.class)
class ItemStackMixin {
    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/serialization/MapCodec;recursive(Ljava/lang/String;Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"
        )
    )
    private static MapCodec<ItemStack> replaceCodec(
        String name,
        Function<Codec<ItemStack>, MapCodec<ItemStack>> function
    ) {
        return MapCodec.recursive(
                "ItemStack",
                codec -> RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Item.ENTRY_CODEC.fieldOf("id").forGetter(ItemStack::getRegistryEntry),
                            Codecs.rangedInt(1, Main.config.get("item.codec.maxStackSize", 64)).fieldOf("count").forGetter(ItemStack::getCount),
                            ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(stack -> stack.components.getChanges())
                        )
                        .apply(instance, ItemStack::new)
                )
            );
    }
}