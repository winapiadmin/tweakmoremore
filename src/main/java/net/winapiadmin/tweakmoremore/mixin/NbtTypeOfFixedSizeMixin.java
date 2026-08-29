package net.winapiadmin.tweakmoremore.mixin;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(NbtType.OfFixedSize.class)
public interface NbtTypeOfFixedSizeMixin {

	@Shadow
	int getSizeInBytes();

	@Inject(method = "skip(Ljava/io/DataInput;ILnet/minecraft/nbt/NbtSizeTracker;)V", at = @At("HEAD"), cancellable = true)
	default void tweakmoremore$skipWithoutIntegerOverflow(DataInput input, int count, NbtSizeTracker tracker, CallbackInfo ci) throws IOException {
		if (!(boolean)Main.config.get("bugfix.NbtType.OfFixedType.assertLength", false)) {
			return;
		}

		if (count < 0) {
			throw new InvalidNbtException("Array count cannot be negative: " + count);
		} else {
			long bytes = (long)count * (long)this.getSizeInBytes();
			tracker.add(bytes);

			while (bytes > 0L) {
				int n = (int)Math.min(bytes, (long)(1 << 20));
				int skipped = input.skipBytes(n);
				if (skipped <= 0) {
					for (int i = 0; i < n; i++) {
						input.readByte();
					}

					bytes -= n;
				} else {
					bytes -= skipped;
				}
			}
		}

		ci.cancel();
	}
}
