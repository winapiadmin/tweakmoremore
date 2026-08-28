package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.List;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.chase.ChaseServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChaseServer.class)
public class ChaseServerMixin {

	@Shadow
	private PlayerManager playerManager;

	@Redirect(
		method = "getTeleportPosition",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"
		)
	)
	private List<ServerPlayerEntity> getDefensivePlayerList(PlayerManager pm) {
                List<ServerPlayerEntity> players=this.playerManager.getPlayerList();
		Object copyPlayerList = Main.config.get("bugfix.ChaseServer.copyPlayerListOnTeleport", false);
		if (copyPlayerList instanceof Boolean copy && copy)
                    return List.copyOf(players);
                else return players;
	}
}
