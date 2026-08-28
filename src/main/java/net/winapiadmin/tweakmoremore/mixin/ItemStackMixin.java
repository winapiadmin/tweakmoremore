package net.winapiadmin.tweakmoremore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import java.util.function.Function;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.ComponentChanges;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.winapiadmin.tweakmoremore.Main;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
class ItemStackMixin {
    @Unique
    private static Collection<? extends Entity> parseSelector(ServerCommandSource source, String selector) throws CommandSyntaxException {
        if (selector.isEmpty())
            return List.of();
        StringReader reader = new StringReader(selector);

        EntityArgumentType arg = EntityArgumentType.entities();
        EntitySelector selectorObj = arg.parse(reader);

        return selectorObj.getEntities(source);
    }
    @Redirect(method = "<clinit>",
              at = @At(value = "INVOKE",
                       target = "Lcom/mojang/serialization/MapCodec;recursive(Ljava/"
                                + "lang/String;Ljava/util/function/Function;)Lcom/"
                                + "mojang/serialization/MapCodec;"))
    private static MapCodec<ItemStack>
    replaceCodec(String name, Function<Codec<ItemStack>, MapCodec<ItemStack>> function) {
        return MapCodec.recursive("ItemStack", codec -> RecordCodecBuilder.mapCodec(instance -> instance.group(Item.ENTRY_CODEC.fieldOf("id").forGetter(ItemStack::getRegistryEntry), Codecs.rangedInt(1, Math.max(1, Main.config.get("item.<any>.maxCountPerStack", 99))).fieldOf("count").forGetter(ItemStack::getCount), ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(stack -> stack.components.getChanges())).apply(instance, ItemStack::new)));
    }
    @Overwrite
    private int calculateDamage(int baseDamage, ServerWorld world, @Nullable ServerPlayerEntity player) throws CommandSyntaxException {
        ItemStack self = (ItemStack)(Object)this;
        int original = 0;
        if (!self.isDamageable()) {
            original = 0;
        } else if (player != null && player.isInCreativeMode()) {
            original = 0;
        } else {
            original = baseDamage > 0 ? EnchantmentHelper.getItemDamage(world, self, baseDamage) : baseDamage;
        }
        String name;
        var id = Registries.ITEM.getId(self.getItem());
        if (id == null) {
            name = "unknown";
        } else {
            name = id.toShortString();
        }
        double mul = Main.config.get("item." + name + ".damageMultiplier", 1.0);

        long scaled = Math.round(original * mul);
        if (scaled > Integer.MAX_VALUE)
            scaled = Integer.MAX_VALUE;
        if (scaled < Integer.MIN_VALUE)
            scaled = Integer.MIN_VALUE;
        String includeEntitiesSelector = Main.config.get("item." + name + ".damageMultiplerPlayersInclude", "");
        String excludeEntitiesSelector = Main.config.get("item." + name + ".damageMultiplerPlayersExclude", "@a");
        ServerCommandSource source = Objects.requireNonNull(world.getServer()).getCommandSource();
        Set<Entity> targets = new HashSet<>();
        try {
            targets.addAll(parseSelector(source, includeEntitiesSelector));
        } catch (CommandSyntaxException e) {
            Main.LOGGER.warn("item.{}.damageMultiplerPlayersInclude option is illegal {}", name, e);
        }
        try {
            parseSelector(source, excludeEntitiesSelector).forEach(targets::remove);
        } catch (CommandSyntaxException e) {
            Main.LOGGER.warn("item.{}.damageMultiplerPlayersExclude option is illegal {}", name, e);
            try {
                targets.addAll(parseSelector(source, "@a"));
            } catch (CommandSyntaxException ignored) {
            }
        }
        if (player instanceof PlayerEntity player_)
            if (!targets.contains(player_))
                return (int)scaled;
        return original;
    }
}
