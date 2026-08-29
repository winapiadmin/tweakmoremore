package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.math.MathHelper;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClampedEntityAttribute.class)
public abstract class ClampedEntityAttributeMixin extends EntityAttribute {

	@Shadow
	@Final
	private double minValue;

	@Shadow
	@Final
	private double maxValue;

	protected ClampedEntityAttributeMixin(String translationKey, double fallback) {
		super(translationKey, fallback);
	}

	/**
	 * @author winapiadmin
	 * @reason Configurable upper bound so mob max health can exceed the vanilla 1024 limit.
	 */
	@Overwrite
	public double clamp(double value) {
		double max = this.maxValue;
		if (this.maxValue == 1024.0D && this.getTranslationKey().equals("attribute.name.max_health")) {
			double configured = (double)Main.config.get("attribute.max_health.max", 1024.0D);
			if (configured > max) {
				max = configured;
			}
		}

		return Double.isNaN(value) ? this.minValue : MathHelper.clamp(value, this.minValue, max);
	}
}
