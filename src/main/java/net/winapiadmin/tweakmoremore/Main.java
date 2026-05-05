package net.winapiadmin.tweakmoremore;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import java.nio.file.Path;
import java.nio.file.Files;
import net.fabricmc.fabric.api.command.v2.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Main implements ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("tweakmoremore");
  public static ModConfig config;

    static {
        try {
            config = ModConfig.read(
                FabricLoader.getInstance().getConfigDir().resolve("tweakmoremore.json"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
  public void onInitialize() {
    ServerWorldEvents.LOAD.register((server, world) -> {
        Path path = server.getSavePath(WorldSavePath.ROOT)
            .resolve("tweakmoremore.json");

        if (Files.exists(path)) {
            try {
                config = ModConfig.read(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                config = ModConfig.read(
                    FabricLoader.getInstance().getConfigDir()
                        .resolve("tweakmoremore.json")
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });
    ServerWorldEvents.UNLOAD.register((server, world) -> {
      if (config!=null) {
          try {
              config.save();
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
      }
    });
    ArgumentTypeRegistry.registerArgumentType(
        Identifier.of("tweakmoremore", "config_key"), ConfigKeyArgument.class,
        ConstantArgumentSerializer.of(ConfigKeyArgument::new));
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment)
            -> dispatcher.register(
                literal("rule")
                    .executes(ctx -> {
                      if (config.isEmpty()) {
                        ctx.getSource().sendFeedback(
                            () -> Text.literal("No rules defined."), false);
                        return 1;
                      }

                      config.forEach(
                          (name, value)
                              -> ctx.getSource().sendFeedback(
                                  ()
                                      -> Text.literal("Rule[" + name +
                                                      "]=" + value),
                                  false));

                      return 1;
                    })
                    .then(
                        argument("name", ConfigKeyArgument.key())
                            .suggests((ctx, builder) -> {
                              String remaining =
                                  builder.getRemaining().toLowerCase();

                              config.keySet()
                                  .stream()
                                  .filter(key
                                          -> key.toLowerCase().startsWith(
                                              remaining))
                                  .sorted() // optional, but keeps it
                                            // predictable
                                  .forEach(builder::suggest);

                              return builder.buildFuture();
                            })
                            .executes(ctx -> {
                              String name =
                                  StringArgumentType.getString(ctx, "name");
                              if (!config.containsKey(name)) {
                                ctx.getSource().sendError(
                                    Text.literal(name + " does not exist"));
                                return 0;
                              }
                              ctx.getSource().sendFeedback(
                                  ()
                                      -> Text.literal("Rule[" + name + "]==" +
                                                      config.get(name)),
                                  true);
                              return 1;
                            })
                            .then(
                                argument("value",
                                         StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                      String name =
                                          StringArgumentType.getString(ctx,
                                                                       "name");
                                      String rawValue =
                                          StringArgumentType.getString(ctx,
                                                                       "value");

                                        // Infer type
                                        Object parsed =
                                            infer(rawValue);
                                        config.set(name, parsed);

                                        ctx.getSource().sendFeedback(
                                            ()
                                                -> Text.literal(
                                                    "Rule[" + name + "]=(" +
                                                    config.get(name) + ")"),
                                            true);

                                      return 1;
                                    })))));
  }

    static Object infer(String s) {
        if (s == null) return null;

        if (s.equalsIgnoreCase("true")) return true;
        if (s.equalsIgnoreCase("false")) return false;

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {}

        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ignored) {}

        return s;
    }
}
