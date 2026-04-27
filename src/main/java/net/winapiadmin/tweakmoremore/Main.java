package net.winapiadmin.tweakmoremore;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.*;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Main implements ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("tweakmoremore");
  public static final ModConfig config = ModConfig.get();
  @Override
  public void onInitialize() {
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
                                      -> Text.literal("Rule[" + name + "]=" +
                                                      value),
                                  false));

                      return 1;
                    })
                    .then(
                        argument("name", ConfigKeyArgument.key())
.suggests((ctx, builder) -> {
  String remaining = builder.getRemaining().toLowerCase();

  config.keySet().stream()
      .filter(key -> key.toLowerCase().startsWith(remaining))
      .sorted() // optional, but keeps it predictable
      .forEach(builder::suggest);

  return builder.buildFuture();
})
                            .executes(ctx -> {
                              String name =
                                  StringArgumentType.getString(ctx, "name");
                              if (config.get(name)==null) {
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
                                argument("value", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                      String name =
                                          StringArgumentType.getString(ctx,
                                                                       "name");
                                      String rawValue =
                                          StringArgumentType.getString(ctx,
                                                                       "value");

                                      RuleType existing = config.getType(name);

                                      if (existing == null) {
                                        // Infer type
                                        RuleType inferred = inferType(rawValue);
                                        Object parsed =
                                            parseValue(inferred, rawValue);

                                        if (parsed == null) {
                                          ctx.getSource().sendError(
                                              Text.literal("Could not infer "
                                                           +
                                                           "type for value: " +
                                                           rawValue));
                                          return 0;
                                        }

                                        config.set(name, inferred, parsed);

                                        ctx.getSource().sendFeedback(
                                            ()
                                                -> Text.literal(
                                                    "Rule[" + name + "]=(" +
                                                    config.get(name) + ")"),
                                            true);

                                      } else {
                                        // Enforce existing type
                                        Object parsed =
                                            parseValue(existing, rawValue);

                                        if (parsed == null) {
                                          ctx.getSource().sendError(
                                              Text.literal(
                                                  "Invalid value for type " +
                                                  existing));
                                          return 0;
                                        }

                                        config.set(name, existing, parsed);

                                        ctx.getSource().sendFeedback(
                                            ()
                                                -> Text.literal(
                                                    "Rule[" + name +
                                                    "]=" + config.get(name)),
                                            true);
                                      }

                                      return 1;
                                    })))));
  }

  private Object parseValue(RuleType type, String raw) {
    try {
      return switch (type) {
        case BOOLEAN -> {
          if (!raw.equalsIgnoreCase("true") && !raw.equalsIgnoreCase("false"))
            yield null;
          yield Boolean.parseBoolean(raw);
        }
        case INTEGER -> Integer.parseInt(raw);
        case FLOAT -> Float.parseFloat(raw);
        case STRING -> raw;
      };
    } catch (Exception e) {
      return null;
    }
  }

  public enum RuleType {
    BOOLEAN,
    INTEGER,
    FLOAT,
    STRING;

    public static RuleType fromString(String s) {
      return switch (s.toLowerCase()) {
        case "bool", "boolean" -> BOOLEAN;
        case "int", "integer" -> INTEGER;
        case "float" -> FLOAT;
        case "str", "string" -> STRING;
        default -> null;
      };
    }
    public static RuleType fromObject(Object o) {
      if (o instanceof Boolean) return BOOLEAN;
      if (o instanceof Integer) return INTEGER;
      if (o instanceof Float) return FLOAT;
      if (o instanceof String) return STRING;

      throw new IllegalArgumentException(
        "Unsupported rule value type: " + o.getClass());
    }
  }
  private RuleType inferType(String raw) {

    // boolean (strict)
    if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false"))
      return RuleType.BOOLEAN;

    // integer (no decimals allowed)
    try {
      Integer.parseInt(raw);
      return RuleType.INTEGER;
    } catch (Exception ignored) {
    }

    // float (must contain dot to avoid ambiguity)
    try {
      if (raw.contains(".")) {
        Float.parseFloat(raw);
        return RuleType.FLOAT;
      }
    } catch (Exception ignored) {
    }

    // fallback string
    return RuleType.STRING;
  }
}
