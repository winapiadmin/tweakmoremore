package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Overwrite
    private static void merge(ItemEntity targetEntity, ItemStack stack1, ItemStack stack2) {
        ItemStack itemStack = ItemEntity.merge(stack1, stack2, Main.config.get("item." + Registries.ITEM.getId(stack1.getItem()).toShortString() + "_stackSize", Main.config.get("item.<any>.maxCountPerStack", 64)));
        targetEntity.setStack(itemStack);
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
    private int modifyDespawnAge(int original) {
        return Main.config.get("item.despawn_age", original);
    }
}
