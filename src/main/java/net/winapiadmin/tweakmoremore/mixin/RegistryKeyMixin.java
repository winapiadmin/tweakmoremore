package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.Objects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RegistryKey.class)
public abstract class RegistryKeyMixin<T> {

	@Shadow
	public abstract Identifier getRegistry();

	@Shadow
	public abstract Identifier getValue();
    //@Overwrite
	@Override
	public boolean equals(Object obj) {
		if (Main.config.get("bugfix.RegistryKey.equalWithObjectOverload", false))
		    return this == obj;
		if (!(obj instanceof RegistryKey<?> other)) return false;
		return Objects.equals(this.getRegistry(), other.getRegistry())
			&& Objects.equals(this.getValue(), other.getValue());
	}

    //@Overwrite
	@Override
	public int hashCode() {
                if (Main.config.get("bugfix.RegistryKey.accurateHashCode", false))
                    return System.identityHashCode(this);
		return Objects.hash(this.getRegistry(), this.getValue());
	}
}
