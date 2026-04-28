package net.winapiadmin.tweakmoremore.mixin;

import static net.minecraft.item.BucketItem.getEmptiedStack;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.winapiadmin.tweakmoremore.Main;
import net.winapiadmin.tweakmoremore.ModConfig;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

  @Shadow @Final private Fluid fluid;

  @Shadow
  public abstract boolean placeFluid(@Nullable LivingEntity user, World world,
                                     BlockPos pos,
                                     @Nullable BlockHitResult hitResult);

  @Shadow
  public abstract void onEmptied(@Nullable LivingEntity user, World world,
                                 ItemStack stack, BlockPos pos);

  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void rewriteUse(World world, PlayerEntity user, Hand hand,
                          CallbackInfoReturnable<ActionResult> cir) {
    ItemStack itemStack = user.getStackInHand(hand);

    BlockHitResult blockHitResult = BucketItem.raycast(
        world, user,
        this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY
                                   : RaycastContext.FluidHandling.NONE);

    if (blockHitResult.getType() != HitResult.Type.BLOCK) {
      cir.setReturnValue(ActionResult.PASS);
      return;
    }

    BlockPos blockPos = blockHitResult.getBlockPos();
    Direction direction = blockHitResult.getSide();
    BlockPos blockPos2 = blockPos.offset(direction);

    if (!world.canEntityModifyAt(user, blockPos) ||
        !user.canPlaceOn(blockPos2, direction, itemStack)) {
      cir.setReturnValue(ActionResult.FAIL);
      return;
    }

    // === Empty bucket (pickup) ===
    if (this.fluid == Fluids.EMPTY) {
      BlockState blockState = world.getBlockState(blockPos);

      if (blockState.getBlock() instanceof FluidDrainable fluidDrainable) {
        ItemStack drained =
            fluidDrainable.tryDrainFluid(user, world, blockPos, blockState);

        if (!drained.isEmpty()) {
          user.incrementStat(
              Stats.USED.getOrCreateStat((BucketItem)(Object)this));
          fluidDrainable.getBucketFillSound().ifPresent(
              sound -> user.playSound(sound, 1.0F, 1.0F));
          world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);

          ItemStack exchanged =
              ItemUsage.exchangeStack(itemStack, user, drained);

          if (!world.isClient() && user instanceof
                                       ServerPlayerEntity serverPlayer) {
            Criteria.FILLED_BUCKET.trigger(serverPlayer, drained);
          }

          cir.setReturnValue(ActionResult.SUCCESS.withNewHandStack(exchanged));
          return;
        }
      }

      cir.setReturnValue(ActionResult.FAIL);
      return;
    }

    // === Filled bucket (placement) ===
    BlockState blockState = world.getBlockState(blockPos);
    BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable &&
                                 this.fluid == Fluids.WATER
                             ? blockPos
                             : blockPos2;

    // Your custom logic hook:
    FluidState existing = world.getFluidState(blockPos3);
    if (existing.isEqualAndStill(this.fluid) &&
        !Main.config.get("allowPlaceSameFluidAndBlock", true)) {

      cir.setReturnValue(ActionResult.FAIL);
      return;
    }

    if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
      this.onEmptied(user, world, itemStack, blockPos3);

      if (user instanceof ServerPlayerEntity serverPlayer) {
        Criteria.PLACED_BLOCK.trigger(serverPlayer, blockPos3, itemStack);
      }

      user.incrementStat(Stats.USED.getOrCreateStat((BucketItem)(Object)this));

      ItemStack exchanged = ItemUsage.exchangeStack(
          itemStack, user, getEmptiedStack(itemStack, user));

      cir.setReturnValue(ActionResult.SUCCESS.withNewHandStack(exchanged));
    } else {
      cir.setReturnValue(ActionResult.FAIL);
    }
  }
}
