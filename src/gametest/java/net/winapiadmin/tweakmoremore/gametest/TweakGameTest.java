package net.winapiadmin.tweakmoremore.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.winapiadmin.tweakmoremore.Main;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class TweakGameTest {

    @GameTest(maxTicks = 40)
    public void configTypeCoercion(TestContext context) {
        Main.config.set("test.boolean_from_string", "true");
        boolean val = Main.config.get("test.boolean_from_string", false);
        context.assertTrue(val, "String 'true' should be parsed as Boolean true");

        Main.config.set("test.integer_from_string", "42");
        int intVal = Main.config.get("test.integer_from_string", 0);
        context.assertEquals(42, intVal, "String '42' should be parsed as Integer 42");

        Main.config.set("test.string_from_boolean", true);
        String strVal = Main.config.get("test.string_from_boolean", "default");
        context.assertEquals("true", strVal, "Boolean true should convert to String 'true'");

        Main.config.set("test.long_from_string", "9999999999");
        long longVal = Main.config.get("test.long_from_string", 0L);
        context.assertEquals(9999999999L, longVal, "String should parse as Long");

        context.complete();
    }

    @GameTest(maxTicks = 20)
    public void blockPlacedCorrectly(TestContext context) {
        BlockPos pos = context.getRelativePos(new BlockPos(0, 1, 0));
        context.setBlockState(pos, Blocks.DIAMOND_BLOCK.getDefaultState());
        context.expectBlock(Blocks.DIAMOND_BLOCK, pos);
        context.complete();
    }
}
