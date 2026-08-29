# API Documentation

## Mod Architecture

tweakmoremore uses Mixin for bytecode manipulation and provides configuration-driven tweaks.

## Core Classes

### Main

Entry point for the mod. Handles config loading, command registration, and initialization.

```java
package net.winapiadmin.tweakmoremore;

public class Main implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("tweakmoremore");
    public static ModConfig config;
}
```

### ModConfig

Manages configuration persistence and type inference.

```java
public class ModConfig {
    public void set(String key, Object value);
    public Object get(String key);
    public <T> T get(String key, T defaultValue);
    public boolean containsKey(String key);
    public void forEach(BiConsumer<String, Object> action);
    public void save();
}
```

### ConfigKeyArgument

Custom Brigadier argument type for config keys with tab completion.

### ForceRandomTickCommand

Registers the `/forcetick` command.

## Mixins

All mixins are located in `net.winapiadmin.tweakmoremore.mixin` package.

### Configuration Access

Mixins access config via:
```java
import net.winapiadmin.tweakmoremore.Main;

// Read config value
int value = Main.config.get("key", defaultValue);
```

### Access Widener

The mod uses `tweakmoremore.accesswidener` to expose internal fields and methods.

## Utility Classes

### DeferredSlotHelper

```java
package net.winapiadmin.tweakmoremore.util;

public class DeferredSlotHelper {
    public static void defer(PlayerInventory inventory, int slot);
    public static int get(PlayerInventory inventory);
    public static void apply(PlayerInventory inventory);
}
```

Used to fix attribute swap issues with selected slot rendering.

### EvalHelper

```java
package net.winapiadmin.tweakmoremore;

public class EvalHelper {
    public static int evaluateInt(String key, String formula, Map<String, Double> vars, int fallback);
}
```

Evaluates mathematical expressions for config values.

## Extending the Mod

### Adding New Config Options

1. Add config key to `Main.java` or appropriate mixin
2. Use `Main.config.get("your.key", defaultValue)` to read
3. Document in configuration.md

### Adding New Mixins

1. Create mixin class in `net.winapiadmin.tweakmoremore.mixin`
2. Add to `tweakmoremore.mixins.json`
3. Update access widener if needed

### Example Mixin

```java
@Mixin(TargetClass.class)
public class ExampleMixin {
    @ModifyConstant(method = "method", constant = @Constant(intValue = 100))
    private int modifyValue(int original) {
        return Main.config.get("example.value", original);
    }
}
```

## Dependencies

- **Fabric Loader**: Mod loader API
- **Fabric API**: Additional Fabric features
- **exp4j**: Mathematical expression evaluation
- **mixinextras**: Additional Mixin utilities (optional)