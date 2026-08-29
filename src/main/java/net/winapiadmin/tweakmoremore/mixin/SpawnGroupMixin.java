package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.SpawnGroup;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpawnGroup.class)
public abstract class SpawnGroupMixin {

    @Shadow private int capacity;

    @Overwrite
    public int getCapacity() {
        return Main.config.get("mob_cap." + ((SpawnGroup)(Object)this).getName(), this.capacity);
    }
}
