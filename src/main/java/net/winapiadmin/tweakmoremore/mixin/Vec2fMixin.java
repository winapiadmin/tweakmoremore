package net.winapiadmin.tweakmoremore.mixin;

import net.winapiadmin.tweakmoremore.Main;
import java.util.Objects;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec2f.class)
public abstract class Vec2fMixin {

	@Shadow
	public final float x;

	@Shadow
	public final float y;

	protected Vec2fMixin(float x, float y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public boolean equals(Object obj) {
		if (Main.config.get("bugfix.Vec2f.equalWithObjectOverload", false))
		    return this == obj;
                if (!(obj instanceof Vec2f other)) return false;
		return Float.floatToIntBits(this.x) == Float.floatToIntBits(other.x)
		    && Float.floatToIntBits(this.y) == Float.floatToIntBits(other.y);
	}

	@Override
	public int hashCode() {
		if (Main.config.get("bugfix.Vec2f.accurateHashCode", false)
				&& Main.config.get("bugfix.Vec2f.equalWithObjectOverload", false))
                    return System.identityHashCode(this);
		return Objects.hash(this.x, this.y);
	}
}
