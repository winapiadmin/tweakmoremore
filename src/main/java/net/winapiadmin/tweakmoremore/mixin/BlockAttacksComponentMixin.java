package net.winapiadmin.tweakmoremore.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.winapiadmin.tweakmoremore.Main;
import net.minecraft.util.Identifier;
import java.util.UUID;
import java.util.*;

@Mixin(BlockAttacksComponent.class)
public abstract class BlockAttacksComponentMixin {
    @Redirect(
        method = "applyShieldCooldown",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/ItemStack;I)V"
        )
    )
    private void redirectCooldownSet(
        ItemCooldownManager instance,
        ItemStack stack,
        int cooldown
    ) {
        String name=Registries.ITEM.getId(stack.getItem()).toShortString();
        Identifier id=this.getGroup(stack);
        if (Main.config.get("attack_blockables."+name+".randomizeGroup", false))
            id=Identifier.of(id.getNamespace(),UUID.randomUUID().toString());
        instance.set(id, Main.config.get("attack_blockables."+name+".cooldown", cooldown));
    }
}