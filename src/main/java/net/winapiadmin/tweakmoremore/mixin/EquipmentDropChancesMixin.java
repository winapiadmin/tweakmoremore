package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.EquipmentDropChances;
import net.minecraft.entity.EquipmentSlot;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquipmentDropChances.class)
public class EquipmentDropChancesMixin {
    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private void onGet(EquipmentSlot slot, CallbackInfoReturnable<Float> cir) {
        float result = cir.getReturnValueF();
        cir.setReturnValue(Main.config.get("equipment.drop_chance", result));
    }
}
