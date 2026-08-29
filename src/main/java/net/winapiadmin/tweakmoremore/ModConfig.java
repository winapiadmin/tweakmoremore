package net.winapiadmin.tweakmoremore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public class ModConfig {
    private int DataVersion;
    private static final int CURRENT_VERSION = 2;
    private final Map<String, Object> values = new HashMap<>();
    private final transient Path path;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

    private static int dirtyCount = 0;
    private static final int SAVE_INTERVAL = 10;

    private ModConfig(Path path) { this.path = path; }

    public void read() {
        values.clear();

        if (!Files.exists(path))
            return;

        try (Reader reader = Files.newBufferedReader(path)) {
            ModConfig loaded = GSON.fromJson(reader, ModConfig.class);

            if (loaded != null) {
                if (loaded.DataVersion != CURRENT_VERSION) {
                    CrashReport report = CrashReport.create(new IllegalStateException("Config version mismatch"), "Loading tweakmoremore config");

                    report.addElement("Config details").add("Expected version", CURRENT_VERSION).add("Actual version", loaded.DataVersion).add("Path", path.toString());

                    throw new CrashException(report);
                }
                values.putAll(loaded.values);
                DataVersion = CURRENT_VERSION;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ModConfig read(Path path) {
        ModConfig config = new ModConfig(path);
        config.read();
        return config;
    }

    public void save() {
        try {
            DataVersion = CURRENT_VERSION;
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void set(String key, Object value) {
        values.put(key, value);

        dirtyCount++;
        if (dirtyCount >= SAVE_INTERVAL) {
            save();
            dirtyCount = 0;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        Object v = values.get(key);

        if (v == null) {
            set(key, defaultValue);
            return defaultValue;
        }

        if (defaultValue instanceof Boolean)
            return (T)(v instanceof Boolean b ? b : v instanceof String s ? Boolean.parseBoolean(s) : defaultValue);
        if (defaultValue instanceof Integer)
            return (T)Integer.valueOf(v instanceof Number n ? n.intValue() : Integer.parseInt(v.toString()));
        if (defaultValue instanceof Float)
            return (T)Float.valueOf(v instanceof Number n ? n.floatValue() : Float.parseFloat(v.toString()));
        if (defaultValue instanceof Double)
            return (T)Double.valueOf(v instanceof Number n ? n.doubleValue() : Double.parseDouble(v.toString()));
        if (defaultValue instanceof Long)
            return (T)Long.valueOf(v instanceof Number n ? n.longValue() : Long.parseLong(v.toString()));
        if (defaultValue instanceof String)
            return (T)(v instanceof String ? v : String.valueOf(v));

        return (T)v;
    }

    public Object get(String key) { return values.get(key); }
    public boolean isEmpty() { return values.isEmpty(); }

    public void forEach(BiConsumer<String, Object> action) { values.forEach(action); }

    public Set<String> keySet() { return values.keySet(); }
    public boolean containsKey(String key) { return values.containsKey(key); }
}
