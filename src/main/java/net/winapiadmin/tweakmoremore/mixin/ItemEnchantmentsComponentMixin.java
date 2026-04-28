package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(ItemEnchantmentsComponent.class)
public class ItemEnchantmentsComponentMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 255))
    public int init(int i) {
        return Main.config.get("enchantment.codec.max_level",255);
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 255))
    private static int clinit(int i) {
        return Main.config.get("enchantment.codec.max_level",255);
    }
}