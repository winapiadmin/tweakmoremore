package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.ServerNetworkIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/ServerNetworkIo$DelayingChannelInboundHandler")
public class ServerNetworkIoMixin {

	@Shadow
	private static io.netty.util.Timer TIMER;

	@Shadow
	private int baseDelay;

	@Shadow
	private int extraDelay;

	@Unique
	private final ConcurrentMap<io.netty.util.Timeout, ServerNetworkIo.DelayingChannelInboundHandler.Packet> mixin$packets = new ConcurrentHashMap<>();
@Unique
private static boolean useConcurrentMap() {
    return Main.config.get(
        "bugfix.ServerNetworkIo.DelayingChannelInboundHandler.useConcurrentMap",
        false
    );
}
	@Redirect(
		method = "<init>(II)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"
		),
		remap = false
	)
	private java.util.List<ServerNetworkIo.DelayingChannelInboundHandler.Packet> noArrayList() {

    if (!useConcurrentMap())
        return com.google.common.collect.Lists.newArrayList();
		return null;
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
		io.netty.util.Timeout timeout = TIMER.newTimeout(t -> {
			ServerNetworkIo.DelayingChannelInboundHandler.Packet p = this.mixin$packets.remove(t);
			if (p != null) {
				p.context.fireChannelRead(p.message);
			}
		}, delay, TimeUnit.MILLISECONDS);
		this.mixin$packets.put(timeout, packet);
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
