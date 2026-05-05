package net.winapiadmin.tweakmoremore.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.winapiadmin.tweakmoremore.Main;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
public class Client implements ClientModInitializer {

    private boolean matches(ItemStack stack, ItemStack target) {
        // item must match
        if (stack.getItem() != target.getItem()) return false;

        // now components: we only check what's present in target
        var targetComps = target.getComponents();
        var stackComps = stack.getComponents();

        for (var type : targetComps.getTypes()) {
            var expected = targetComps.get(type);
            var actual = stackComps.get(type);

            if (actual == null) return false;

            if (!actual.equals(expected)) return false;
        }

        return true;
    }
    @Override
    public void onInitializeClient() {
        if (Main.config.get("cheats.commands.swap", false)) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("swap")
                    .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                            .executes(context -> {
                                Entity e = context.getSource().getEntity();
                                if (e instanceof PlayerEntity player) {
                                    var arg = ItemStackArgumentType.getItemStackArgument(context, "item");
                                    ItemStack target = arg.createStack(1, false);
                                    PlayerInventory inv = player.getInventory();
                                    for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
                                        ItemStack stack = inv.getStack(i);
                                        if (!stack.isEmpty() && matches(target, stack)) {
                                            inv.setSelectedSlot(i);

                                            context.getSource().sendFeedback(
                                                    Text.empty()
                                                            .append(player.getName())
                                                            .append(Text.literal(" swapped to an item with component "))
                                                            .append(target.toHoverableText())
                                                            .append(Text.literal(" (index "+i+")"))
                                            );
                                            return 1;
                                        }
                                    }
                                    context.getSource().sendError(
                                            Text.empty()
                                                    .append(player.getName())
                                                    .append(Text.literal(" failed to swap to an item with component "))
                                                    .append(target.toHoverableText())
                                    );
                                }
                                return 0;
                            })
                    )
            ));
        }
        if (Main.config.    get("cheats.ender_pearl_target", false)) {
            ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
                if (entity.getType() == EntityType.ENDER_PEARL){
                    EnderPearlEntity pearl=(EnderPearlEntity)entity;
                    Entity owner=pearl.getOwner();
                    PlayerEntity clientPlayer=MinecraftClient.getInstance().player;
                    if (owner instanceof PlayerEntity player){
                        if (!player.equals(clientPlayer) && clientPlayer!=null)
                        {
                            Vec3d pos = pearl.getEntityPos();
                            Vec3d velocity = pearl.getVelocity();

                            World entityWorld = pearl.getEntityWorld();
                            // not in same dimension
                            if (!entityWorld.getRegistryKey().equals(world.getRegistryKey())) return;
                            for (int i = 0; i < 200; i++) { // simulate ~10 seconds (200 ticks)
                                Vec3d nextPos = pos.add(velocity);

                                HitResult hit = entityWorld.raycast(new RaycastContext(
                                        pos,
                                        nextPos,
                                        RaycastContext.ShapeType.COLLIDER,
                                        RaycastContext.FluidHandling.NONE,
                                        pearl
                                ));

                                if (hit.getType() != HitResult.Type.MISS) {
                                    Vec3d p=hit.getPos();
                                    String cmd = String.format("/tp @s %.2f %.2f %.2f", p.x, p.y, p.z);

                                    Text text = Text.literal("Pearl landing: ")
                                            .append(Text.literal(String.format(
                                                            "%.2f, %.2f, %.2f", p.x, p.y, p.z
                                                    ))
                                                    .styled(style -> style
                                                            .withColor(Formatting.AQUA)
                                                            .withClickEvent(new ClickEvent.RunCommand(cmd))
                                                            .withHoverEvent(new HoverEvent.ShowText(
                                                                    Text.literal("Click to teleport")))));
                                    player.sendMessage(text, false);
                                    break;
                                }

                                pos = nextPos;

                                // apply drag + gravity (vanilla-ish)
                                velocity = velocity.multiply(0.99);
                                if (!pearl.hasNoGravity()) {
                                    velocity = velocity.add(0, -0.03, 0);
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}
