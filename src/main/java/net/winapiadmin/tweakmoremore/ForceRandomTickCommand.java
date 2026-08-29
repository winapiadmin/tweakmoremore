package net.winapiadmin.tweakmoremore;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
public class ForceRandomTickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("forcetick").requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK)).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(ctx -> {
            if (!Main.config.get("commands.forceRandomTick", false)) {
                ctx.getSource().sendError(Text.literal("This command is disabled."));
                return 0;
            }
            ServerWorld world = ctx.getSource().getWorld();
            BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "pos");

            BlockState state = world.getBlockState(pos);

            if (state.hasRandomTicks()) {
                state.randomTick(world, pos, world.random);
            }

            FluidState fluid = state.getFluidState();

            if (fluid.hasRandomTicks()) {
                fluid.onRandomTick(world, pos, world.random);
            }

            MutableText posText = Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ()))
                                      .styled(style
                                              -> style.withColor(Formatting.GREEN)
                                                     .withClickEvent(new ClickEvent.SuggestCommand("/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))
                                                     .withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.coordinates."
                                                                                                               + "tooltip"))));

            ctx.getSource().sendFeedback(() -> Text.literal("Forced random tick on ").append(state.getBlock().getName()).append(" at ").append(posText), true);

            return 1;
        })));
    }
}