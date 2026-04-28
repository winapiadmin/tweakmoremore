package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(ItemEnchantmentsComponent.Builder.class)
public class ItemEnchantmentsComponentBuilderMixin {
    @ModifyConstant(method = "set", constant = @Constant(intValue = 255))
    private static int setEnchantment(int i) {
        return Main.config.get("enchantment.codec.max_level",255);
    }
    @ModifyConstant(method = "add", constant = @Constant(intValue = 255))
    private static int addEnchantment(int i) {
        return Main.config.get("enchantment.codec.max_level",255);
    }
}