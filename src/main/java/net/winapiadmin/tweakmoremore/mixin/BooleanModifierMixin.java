package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.Objects;
import net.minecraft.world.attribute.BooleanModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BooleanModifier.class)
public class BooleanModifierMixin {

	@Inject(method = "apply(Ljava/lang/Boolean;Ljava/lang/Boolean;)Ljava/lang/Boolean;", at = @At("RETURN"), cancellable = true)
	private void fixXNOR(Boolean boolean_, Boolean boolean2, CallbackInfoReturnable<Boolean> cir) {
		if (Main.config.get("bugfix.BooleanModifier.correctXnorOp", false))
                    if ((BooleanModifier)(Object)this == BooleanModifier.XNOR)
			cir.setReturnValue(!(boolean_ ^ boolean2));
	}
}
