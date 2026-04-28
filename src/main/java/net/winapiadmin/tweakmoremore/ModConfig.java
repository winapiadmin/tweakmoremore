package net.winapiadmin.tweakmoremore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
public class ModConfig {

  public Map<String, Boolean> booleans = new HashMap<>();

  public Map<String, Integer> integers = new HashMap<>();

  public Map<String, Float> floats = new HashMap<>();

  public Map<String, String> strings = new HashMap<>();
  private transient Path path;
  private static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().create();
  private static int dirtyCount = 0;
  private static final int SAVE_INTERVAL = 1000;
  private ModConfig(Path path) { this.path = path; }

  public boolean read() {
    this.booleans.clear();
    this.integers.clear();
    this.floats.clear();
    this.strings.clear();
    try {
      if (!Files.exists(path)) {
        return false;
      }

      try (Reader reader = Files.newBufferedReader(path)) {
        ModConfig loaded = GSON.fromJson(reader, ModConfig.class);

        if (loaded != null) {
          if (loaded.booleans != null)
            this.booleans.putAll(loaded.booleans);
          if (loaded.integers != null)
            this.integers.putAll(loaded.integers);
          if (loaded.floats != null)
            this.floats.putAll(loaded.floats);
          if (loaded.strings != null)
            this.strings.putAll(loaded.strings);
          return true;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }
  public static ModConfig read(Path path) {
    ModConfig config = new ModConfig(path);
    config.read();
    return config;
  }

  public void save() {
    try {
      Files.createDirectories(path.getParent());

      try (Writer writer = Files.newBufferedWriter(this.path)) {
        GSON.toJson(this, writer);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Main.RuleType getType(String key) {
    if (booleans.containsKey(key))
      return Main.RuleType.BOOLEAN;
    if (integers.containsKey(key))
      return Main.RuleType.INTEGER;
    if (floats.containsKey(key))
      return Main.RuleType.FLOAT;
    if (strings.containsKey(key))
      return Main.RuleType.STRING;
    return null;
  }

  public Object getRaw(String key) {
    if (booleans.containsKey(key))
      return booleans.get(key);
    if (integers.containsKey(key))
      return integers.get(key);
    if (floats.containsKey(key))
      return floats.get(key);
    if (strings.containsKey(key))
      return strings.get(key);
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

    dirtyCount++;

    if (dirtyCount >= SAVE_INTERVAL) {
      save();
      dirtyCount = 0;
    }
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
    return (T)value;
  }
  public boolean isEmpty() {
    return booleans.isEmpty() && integers.isEmpty() && floats.isEmpty() &&
        strings.isEmpty();
  }
  public void forEach(BiConsumer<String, Object> action) {
    booleans.forEach(action);
    integers.forEach(action);
    strings.forEach(action);
    floats.forEach(action);
  }
  public Set<String> keySet() {

    Set<String> allKeys = new HashSet<>();

    allKeys.addAll(booleans.keySet());
    allKeys.addAll(integers.keySet());
    allKeys.addAll(strings.keySet());
    allKeys.addAll(floats.keySet());
    return allKeys;
  }
}