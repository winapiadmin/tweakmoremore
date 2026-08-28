package net.winapiadmin.tweakmoremore.mixin;

import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.winapiadmin.tweakmoremore.Main;

@Mixin(AbstractBlock.class)
public abstract class BlockDropComponentsMixin {

	@Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootWorldContext$Builder;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
	private void mixin$retainBlockEntityComponents(BlockState state, LootWorldContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
		if (!Main.config.get("bugfix.block.retainBlockEntityComponents", false)) return;
		List<ItemStack> stacks = cir.getReturnValue();
		if (stacks.isEmpty()) {
			return;
		}

		BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
		if (blockEntity == null) {
			return;
		}

		ComponentMap componentMap = blockEntity.createComponentMap();
		for (ItemStack stack : stacks) {
			stack.applyComponentsFrom(componentMap.filtered(type -> mixin$isNotExcluded(type) && stack.get(type) == null));
		}

		cir.setReturnValue(stacks);
	}

	@Unique
	private static boolean mixin$isNotExcluded(ComponentType<?> type) {
		return type != DataComponentTypes.CONTAINER
			&& type != DataComponentTypes.CONTAINER_LOOT
			&& type != DataComponentTypes.BLOCK_ENTITY_DATA
			&& type != DataComponentTypes.BLOCK_STATE;
	}
}
