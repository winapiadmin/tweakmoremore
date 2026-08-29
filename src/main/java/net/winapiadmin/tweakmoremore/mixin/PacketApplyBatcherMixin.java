package net.winapiadmin.tweakmoremore.mixin;

import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import net.minecraft.network.PacketApplyBatcher;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(PacketApplyBatcher.class)
public class PacketApplyBatcherMixin {

	@Shadow
	private Queue<?> entries;

	@Inject(method = "add", at = @At("HEAD"))
	private <T extends PacketListener> void onAdd(T listener, Packet<T> packet, CallbackInfo ci) {
		int limit = Main.config.get("bugfix.PacketApplyBatcher.limit", Integer.MAX_VALUE);
		if (this.entries.size() >= limit) {
			throw new RejectedExecutionException(
				"Packet queue overflow: more than " + limit + " packets queued for the main thread"
			);
		}
	}
}