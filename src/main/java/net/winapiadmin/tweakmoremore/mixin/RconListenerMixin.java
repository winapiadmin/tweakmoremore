package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.server.rcon.RconClient;
import net.minecraft.server.rcon.RconListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RconListener.class)
public class RconListenerMixin {

	@Shadow
	@Final
	@Mutable
	private List<RconClient> clients;

	@Inject(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/rcon/RconListener;clients:Ljava/util/List;",
			opcode = Opcodes.PUTFIELD,
			shift = At.Shift.AFTER
		)
	)
	private void createThreadSafeClientList(CallbackInfo ci) {
		Object val = Main.config.get("bugfix.RconListener.useCOWArrayList");
		if (val instanceof Boolean b && b) {
			this.clients = new CopyOnWriteArrayList<>();
		}
	}
}
