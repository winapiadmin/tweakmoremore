package net.winapiadmin.tweakmoremore;

import java.util.*;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import java.util.function.*;
@Config(name = "tweakmoremore")
public class ModConfig implements ConfigData {

  @ConfigEntry.Gui.CollapsibleObject
  public Map<String, Boolean> booleans = new HashMap<>();

  @ConfigEntry.Gui.CollapsibleObject
  public Map<String, Integer> integers = new HashMap<>();

  @ConfigEntry.Gui.CollapsibleObject
  public Map<String, Float> floats = new HashMap<>();

  @ConfigEntry.Gui.CollapsibleObject
  public Map<String, String> strings = new HashMap<>();

  private static boolean registered = false;

  private static void ensureRegistered() {
    if (!registered) {
      AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
      registered = true;
    }
  }

  private ModConfig() {}

  public static ModConfig get() {
    ensureRegistered();
    return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
  }

  private void save() {
    AutoConfig.getConfigHolder(ModConfig.class).save();
  }

  public Main.RuleType getType(String key) {
    if (booleans.containsKey(key)) return Main.RuleType.BOOLEAN;
    if (integers.containsKey(key)) return Main.RuleType.INTEGER;
    if (floats.containsKey(key)) return Main.RuleType.FLOAT;
    if (strings.containsKey(key)) return Main.RuleType.STRING;
    return null;
  }

  public Object getRaw(String key) {
    if (booleans.containsKey(key)) return booleans.get(key);
    if (integers.containsKey(key)) return integers.get(key);
    if (floats.containsKey(key)) return floats.get(key);
    if (strings.containsKey(key)) return strings.get(key);
    return null;
  }

  public void set(String key, Main.RuleType type, Object value) {
    setTyped(key, type.name(), value);
  }

  public void setTyped(String key, String type, Object value) {
    Main.RuleType ruleType = Main.RuleType.fromString(type);

    switch (ruleType) {
      case BOOLEAN -> {
        if (!(value instanceof Boolean b))
          throw new IllegalArgumentException("Expected Boolean");
        booleans.put(key, b);
      }

      case INTEGER -> {
        if (!(value instanceof Number n))
          throw new IllegalArgumentException("Expected Number");
        integers.put(key, n.intValue());
      }

      case FLOAT -> {
        if (!(value instanceof Number n))
          throw new IllegalArgumentException("Expected Number");
        floats.put(key, n.floatValue());
      }

      case STRING -> {
        if (!(value instanceof String s))
          throw new IllegalArgumentException("Expected String");
        strings.put(key, s);
      }
    }

    save();
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key, T defaultValue) {

    Object value = getRaw(key);

    if (value == null) {
      set(key, Main.RuleType.fromObject((Object)defaultValue), defaultValue);
      return defaultValue;
    }

    Class<?> clazz = defaultValue.getClass();

    return switch (value) {
      case Number n when clazz == Integer.class ->
        (T) Integer.valueOf(n.intValue());

      case Number n when clazz == Float.class ->
        (T) Float.valueOf(n.floatValue());

      case Number n when clazz == Double.class ->
        (T) Double.valueOf(n.doubleValue());

      case Boolean b when clazz == Boolean.class -> (T) b;

      default -> (T) value;
    };
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    Object value = getRaw(key);
    if (value == null)
      return null;
    return (T) value;
  }
  public boolean isEmpty(){
    return booleans.isEmpty() && integers.isEmpty() && floats.isEmpty() && strings.isEmpty();
  }
  public void forEach(BiConsumer<String, Object> action){
    booleans.forEach(action);
    integers.forEach(action);
    strings.forEach(action);
    floats.forEach(action);
  }
  public Set<String> keySet(){
    
    Set<String> allKeys = new HashSet<>();
    
    allKeys.addAll(booleans.keySet());
    allKeys.addAll(integers.keySet());
    allKeys.addAll(strings.keySet());
    allKeys.addAll(floats.keySet());
    return allKeys;
  }
}