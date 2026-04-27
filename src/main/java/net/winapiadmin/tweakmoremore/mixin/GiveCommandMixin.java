package net.winapiadmin.tweakmoremore.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.server.command.GiveCommand.class)
class GiveCommandMixin{
@org.spongepowered.asm.mixin.injection.ModifyVariable(
    method = "execute",
    at = @org.spongepowered.asm.mixin.injection.At("STORE"),
    ordinal = 1
)
private static int fixLimit(int j, net.minecraft.server.command.ServerCommandSource source,
                            net.minecraft.command.argument.ItemStackArgument item,
                            java.util.Collection<net.minecraft.server.network.ServerPlayerEntity> targets,
                            int count, @com.llamalad7.mixinextras.sugar.Local(ordinal = 0, argsOnly = true) int i) {
    long safe = (long) i * 100L;

    return safe > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) safe;
}
}