package net.winapiadmin.tweakmoremore.mixin;

import io.netty.handler.codec.DecoderException;
import java.util.function.IntFunction;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {

	@Unique
	private static final int MAX_COLLECTION_SIZE = 1 << 20;

	@Redirect(
		method = {"readCollection", "readMap"},
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/function/IntFunction;apply(I)Ljava/lang/Object;",
			remap = false
		)
	)
	private Object checkCollectionSize(IntFunction<?> factory, int size) {
		if (Main.config.get("bugfix.PacketByteBuf.assertSize",false) && (size < 0 || size > MAX_COLLECTION_SIZE)) {
			throw new DecoderException("Collection size " + size + " is outside allowed range [0, " + MAX_COLLECTION_SIZE + "]");
		} else {
			return factory.apply(size);
		}
	}
}