package net.winapiadmin.tweakmoremore.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.SpawnHelper;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpawnHelper.Info.class)
public abstract class SpawnHelperMixin {

    @Shadow private int spawningChunkCount;

    @Shadow private Object2IntOpenHashMap<SpawnGroup> groupToCount;

    @Overwrite
    public boolean isBelowCap(SpawnGroup group) {
        int cap = group.getCapacity();
        int area = Main.config.get("mob_cap.chunk_area", 289);
        int effective = cap * this.spawningChunkCount / Math.max(area, 1);
        return this.groupToCount.getInt(group) < effective;
    }
}
