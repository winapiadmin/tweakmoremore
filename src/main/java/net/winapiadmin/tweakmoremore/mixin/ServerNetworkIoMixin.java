package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.ServerNetworkIo;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/ServerNetworkIo$DelayingChannelInboundHandler")
public class ServerNetworkIoMixin {

	@Shadow
	private static io.netty.util.Timer TIMER;

	@Shadow
	private int baseDelay;

	@Shadow
	private int extraDelay;

	@Shadow
	@Final
	@Mutable
	private List<ServerNetworkIo.DelayingChannelInboundHandler.Packet> packets;

@Unique
private static boolean useConcurrentMap() {
    return Main.config.get(
        "bugfix.ServerNetworkIo.DelayingChannelInboundHandler.useConcurrentMap",
        false
    );
}
	@Inject(
		method = "<init>(II)V",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/ServerNetworkIo$DelayingChannelInboundHandler;packets:Ljava/util/List;",
			opcode = Opcodes.PUTFIELD,
			shift = At.Shift.AFTER
		)
	)
	private void noArrayList(CallbackInfo ci) {
		if (useConcurrentMap()) {
			this.packets = null;
		}
	}

	@Inject(
		method = "delay(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Object;)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onDelay(io.netty.channel.ChannelHandlerContext ctx, Object msg, CallbackInfo ci) {
    if (!useConcurrentMap()) return;
		ServerNetworkIo.DelayingChannelInboundHandler.Packet packet =
			new ServerNetworkIo.DelayingChannelInboundHandler.Packet(ctx, msg);
		int delay = this.baseDelay + (int)(Math.random() * this.extraDelay);
		TIMER.newTimeout(t -> packet.context.fireChannelRead(packet.message), delay, TimeUnit.MILLISECONDS);
		ci.cancel();
	}

	@Inject(
		method = "forward(Lio/netty/util/Timeout;)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onForward(io.netty.util.Timeout timeout, CallbackInfo ci) {
    if (!useConcurrentMap()) return;
		ci.cancel();
	}
}
