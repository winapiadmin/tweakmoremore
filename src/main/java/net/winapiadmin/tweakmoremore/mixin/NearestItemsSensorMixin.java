package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.ai.brain.sensor.NearestItemsSensor;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NearestItemsSensor.class)
public abstract class NearestItemsSensorMixin {

    @ModifyConstant(method = "sense", constant = @Constant(doubleValue = 32.0, ordinal = 0))
    private double overrideHorizontalRangeX(double original) {
        return Main.config.get("villager.item_sense_horizontal", 32);
    }

    @ModifyConstant(method = "sense", constant = @Constant(doubleValue = 16.0))
    private double overrideVerticalRange(double original) {
        return Main.config.get("villager.item_sense_vertical", 16);
    }

    @ModifyConstant(method = "sense", constant = @Constant(doubleValue = 32.0, ordinal = 1))
    private double overrideHorizontalRangeZ(double original) {
        return Main.config.get("villager.item_sense_horizontal", 32);
    }
}
