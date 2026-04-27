package net.winapiadmin.tweakmoremore.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(StackWithSlot.class)
public class StackWithSlotMixin {

    @Shadow @Final @Mutable
    public static Codec<StackWithSlot> CODEC;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceCodec(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("Slot").orElse(0)
                    .forGetter(StackWithSlot::slot),
                ItemStack.MAP_CODEC
                    .forGetter(StackWithSlot::stack)
            ).apply(instance, StackWithSlot::new)
        );
    }
}

