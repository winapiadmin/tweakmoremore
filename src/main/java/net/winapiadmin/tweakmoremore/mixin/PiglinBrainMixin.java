package net.winapiadmin.tweakmoremore.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AdmireItemTask;
import net.minecraft.entity.ai.brain.task.AdmireItemTimeLimitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WantNewItemTask;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Overwrite
    private static void addAdmireItemActivities(Brain<PiglinEntity> brain) {
        brain.setTaskList(Activity.ADMIRE_ITEM,
                          Main.config.get("entity.piglin.admireGoldPriority", 10),
                          ImmutableList.of(WalkTowardsNearestVisibleWantedItemTask.create(PiglinBrain::doesNotHaveGoldInOffHand, Main.config.get("entity.piglin.findGoldSpeedModifier", 1.0F), true, Main.config.get("entity.piglin.findGoldRadius", 9)), WantNewItemTask.create(Main.config.get("entity.piglin.findGoldRadius", 9)), AdmireItemTimeLimitTask.create(Main.config.get("entity.piglin.findGoldTimeTicks", 200), Main.config.get("entity.piglin.refuseTradeCooldownTicks", 200))),
                          MemoryModuleType.ADMIRING_ITEM);
    }
    @ModifyConstant(method = "addCoreActivities", constant = @Constant(intValue = 119))
    private static int tradeTime(int original) {
        return Main.config.get("entity.piglin.tradeTime", original);
    }
}
