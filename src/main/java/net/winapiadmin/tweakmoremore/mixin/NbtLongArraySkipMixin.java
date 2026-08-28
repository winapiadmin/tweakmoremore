package net.winapiadmin.tweakmoremore.mixin;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtSizeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Overwrite;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(targets = "net/minecraft/nbt/NbtLongArray$1")
public class NbtLongArraySkipMixin {

	@Unique
	private static final int SKIP_CHUNK = 1 << 20;

	@Overwrite
	public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
                if (!Main.config.get("bugfix.NbtLongArray.assertLength",false)){
                        input.skipBytes(input.readInt()*8);return;
                }
		int length = input.readInt();
		if (length < 0) {
			throw new InvalidNbtException("Array length cannot be negative: " + length);
		} else {
			long bytes = (long)length * 8L;
			tracker.add(bytes);
			mixin$skipChunked(input, bytes);
		}
	}

	@Unique
	private static void mixin$skipChunked(DataInput input, long bytes) throws IOException {
		while (bytes > 0L) {
			int n = (int)Math.min(bytes, (long)SKIP_CHUNK);
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
}