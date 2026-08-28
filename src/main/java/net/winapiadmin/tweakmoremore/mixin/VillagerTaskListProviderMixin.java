package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(VillagerTaskListProvider.class)
public abstract class VillagerTaskListProviderMixin {

    @ModifyConstant(method = "createCoreTasks", constant = @Constant(intValue = 4))
    private static int overrideItemPickupRange(int original) {
        return Main.config.get("villager.item_pickup_range", original);
    }
}
