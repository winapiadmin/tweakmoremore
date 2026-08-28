package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.server.rcon.RconClient;
import net.minecraft.server.rcon.RconListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RconListener.class)
public class RconListenerMixin {

	@Redirect(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"
		),
		remap = false
	)
	private List<RconClient> createThreadSafeClientList() {
                if (!Main.config.get("bugfix.RconListener.useCOWArrayList",false))
                    return com.google.common.collect.Lists.newArrayList();
		return new CopyOnWriteArrayList<>();
	}
}
