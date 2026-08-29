package net.winapiadmin.tweakmoremore;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ModConfigTest {

    @TempDir
    Path tempDir;

    private ModConfig config;

    @BeforeEach
    void setUp() throws IOException {
        Path file = tempDir.resolve("test.json");
        Files.writeString(file, "{\"DataVersion\":2,\"values\":{}}");
        config = ModConfig.read(file);
        Main.config = config;
    }

    // ── get() Boolean branch ──

    @Test
    void booleanDefault_withBooleanValue_returnsBoolean() {
        config.set("key", true);
        assertTrue(config.get("key", false));
    }

    @Test
    void booleanDefault_withStringTrue_parsesTrue() {
        config.set("key", "true");
        assertTrue(config.get("key", false));
    }

    @Test
    void booleanDefault_withStringFalse_parsesFalse() {
        config.set("key", "false");
        assertFalse(config.get("key", true));
    }

    @Test
    void booleanDefault_withStringGarbage_returnsFalse() {
        config.set("key", "notabool");
        assertFalse(config.get("key", false));
    }

    @Test
    void booleanDefault_withStringGarbage_defaultTrue_returnsFalse() {
        config.set("key", "notabool");
        // Boolean.parseBoolean returns false for anything that isn't "true"
        assertFalse(config.get("key", true));
    }

    @Test
    void booleanDefault_withInteger1_returnsFalse() {
        config.set("key", 1);
        assertFalse(config.get("key", false));
    }

    // ── get() Integer branch ──

    @Test
    void integerDefault_withIntegerValue_returnsInteger() {
        config.set("key", 42);
        assertEquals(42, config.get("key", 0));
    }

    @Test
    void integerDefault_withStringValue_parsesInteger() {
        config.set("key", "42");
        assertEquals(42, config.get("key", 0));
    }

    @Test
    void integerDefault_withStringValue_throwsOnBadNumber() {
        config.set("key", "abc");
        assertThrows(NumberFormatException.class, () -> config.get("key", 0));
    }

    @Test
    void integerDefault_withDoubleValue_truncates() {
        config.set("key", 3.7);
        assertEquals(3, config.get("key", 0));
    }

    // ── get() Double branch ──

    @Test
    void doubleDefault_withDoubleValue_returnsDouble() {
        config.set("key", 3.14);
        assertEquals(3.14, config.get("key", 0.0));
    }

    @Test
    void doubleDefault_withStringValue_parsesDouble() {
        config.set("key", "2.718");
        assertEquals(2.718, config.get("key", 0.0));
    }

    // ── get() String branch ──

    @Test
    void stringDefault_withStringValue_returnsString() {
        config.set("key", "hello");
        assertEquals("hello", config.get("key", "default"));
    }

    @Test
    void stringDefault_withBooleanValue_convertsToString() {
        config.set("key", true);
        assertEquals("true", config.get("key", "default"));
    }

    @Test
    void stringDefault_withIntegerValue_convertsToString() {
        config.set("key", 42);
        assertEquals("42", config.get("key", "default"));
    }

    @Test
    void stringDefault_withDoubleValue_convertsToString() {
        config.set("key", 3.14);
        assertEquals("3.14", config.get("key", "default"));
    }

    // ── get() null / missing key ──

    @Test
    void missingKey_autoWritesDefault() {
        assertEquals("fallback", config.get("missing", "fallback"));
        assertEquals("fallback", config.get("missing", "fallback"));
    }

    @Test
    void missingBooleanKey_autoWritesDefault() {
        assertFalse(config.get("missing_bool", false));
        assertTrue(config.get("missing_bool_true", true));
    }

    // ── infer() ──

    @Test
    void infer_newKey_returnsString() {
        Object result = Main.infer("hello", "brand_new_key");
        assertEquals("hello", result);
    }

    @Test
    void infer_newKey_numeric_returnsString() {
        Object result = Main.infer("42", "brand_new_key");
        assertEquals("42", result);
    }

    @Test
    void infer_newKey_true_returnsBoolean() {
        Object result = Main.infer("true", "brand_new_key");
        assertEquals(true, result);
    }

    @Test
    void infer_newKey_false_returnsBoolean() {
        Object result = Main.infer("false", "brand_new_key");
        assertEquals(false, result);
    }

    @Test
    void infer_existingBooleanKey_validInput() {
        config.set("bool_key", true);
        Object result = Main.infer("false", "bool_key");
        assertEquals(false, result);
    }

    @Test
    void infer_existingBooleanKey_invalidInput_throws() {
        config.set("bool_key", true);
        assertThrows(IllegalArgumentException.class, () -> Main.infer("yes", "bool_key"));
    }

    @Test
    void infer_existingIntegerKey_validInput() {
        config.set("int_key", 10);
        Object result = Main.infer("99", "int_key");
        assertEquals(99, result);
    }

    @Test
    void infer_existingIntegerKey_invalidInput_throws() {
        config.set("int_key", 10);
        assertThrows(NumberFormatException.class, () -> Main.infer("abc", "int_key"));
    }

    // ── Long branch ──

    @Test
    void longDefault_withLongValue_returnsLong() {
        config.set("key", 9999999999L);
        assertEquals(9999999999L, config.get("key", 0L));
    }

    @Test
    void longDefault_withStringValue_parsesLong() {
        config.set("key", "9999999999");
        assertEquals(9999999999L, config.get("key", 0L));
    }
}
