package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.intprovider.IntProvider;
import net.winapiadmin.tweakmoremore.ExperienceDroppingBlockAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExperienceDroppingBlock.class)
public abstract class ExperienceDroppingBlockMixin
        implements ExperienceDroppingBlockAccessor {

    @Shadow @Final @Mutable
    private IntProvider experienceDropped;

    @Override
    public void tweakmoremore$setExperienceDropped(IntProvider provider) {
        this.experienceDropped = provider;
    }
}