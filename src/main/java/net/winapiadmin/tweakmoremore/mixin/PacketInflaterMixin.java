package net.winapiadmin.tweakmoremore.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.handler.PacketInflater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(PacketInflater.class)
public class PacketInflaterMixin {

	@Redirect(
		method = "inflate",
		at = @At(
			value = "INVOKE",
			target = "Lio/netty/buffer/ByteBufAllocator;directBuffer(I)Lio/netty/buffer/ByteBuf;",
			remap = false
		)
	)
	private ByteBuf clampUncompressedAllocation(ByteBufAllocator allocator, int expectedSize) {
		if (Main.config.get("bugfix.PacketInflater.assertLength",false) && expectedSize > PacketInflater.MAXIMUM_PACKET_SIZE) {
			throw new DecoderException(
				"Badly compressed packet - declared uncompressed size of " + expectedSize + " is larger than protocol maximum of "
					+ PacketInflater.MAXIMUM_PACKET_SIZE
			);
		} else {
			return allocator.directBuffer(expectedSize);
		}
	}
}
