package net.winapiadmin.tweakmoremore.mixin;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.dynamic.Codecs;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.Definition.class)
public class EnchantmentDefinitionMixin {

    @Mutable
    @Shadow
    @Final
    public static MapCodec<Enchantment.Definition> CODEC;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void tweakCodec(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("supported_items").forGetter(Enchantment.Definition::supportedItems),
                        RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("primary_items").forGetter(Enchantment.Definition::primaryItems),

                        Codecs.rangedInt(1, Main.config.get("enchantment.codec.weight",1024)).fieldOf("weight").forGetter(Enchantment.Definition::weight),

                        // your change
                        Codecs.rangedInt(1, Main.config.get("enchantment.codec.max_level",255)).fieldOf("max_level").forGetter(Enchantment.Definition::maxLevel),

                        Enchantment.Cost.CODEC.fieldOf("min_cost").forGetter(Enchantment.Definition::minCost),
                        Enchantment.Cost.CODEC.fieldOf("max_cost").forGetter(Enchantment.Definition::maxCost),

                        Codecs.NON_NEGATIVE_INT.fieldOf("anvil_cost").forGetter(Enchantment.Definition::anvilCost),
                        AttributeModifierSlot.CODEC.listOf().fieldOf("slots").forGetter(Enchantment.Definition::slots)
                ).apply(instance, Enchantment.Definition::new)
        );
    }
}
