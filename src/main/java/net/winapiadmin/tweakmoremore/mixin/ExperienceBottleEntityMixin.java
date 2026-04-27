package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.server.world.ServerWorld;
import net.winapiadmin.tweakmoremore.Main;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(ExperienceBottleEntity.class)
public abstract class ExperienceBottleEntityMixin {

    @Unique
    private static final String DEFAULT_EQ = "3+randInt(0,5)+randInt(0,5)";

    @Unique
    private static String lastBad = null;

    // --- Functions using Minecraft RNG ---

    @Unique
    private static Function random0(ServerWorld world) {
        return new Function("random", 0) {
            @Override
            public double apply(double... args) {
                return world.random.nextDouble();
            }
        };
    }

    @Unique
    private static Function random2(ServerWorld world) {
        return new Function("random", 2) {
            @Override
            public double apply(double... args) {
                double min = args[0];
                double max = args[1];
                return min + world.random.nextDouble() * (max - min);
            }
        };
    }

    @Unique
    private static Function randInt(ServerWorld world) {
        return new Function("randInt", 2) {
        @Override
        public double apply(double... args) {
            int min = (int) args[0];
            int max = (int) args[1];
            if (max <= min) return min;
            return world.random.nextInt(max - min) + min;
            }
        };
    }
    // --- Eval ---

    @Unique
    private static int eval(String formula, ServerWorld world) {
        try {
            Expression e = new ExpressionBuilder(formula)
                    .function(random0(world))
                    .function(random2(world))
                    .function(randInt(world))      // randInt(a,b)
                    .build();

            double r = e.evaluate();
            if (!Double.isFinite(r)) throw new RuntimeException();

            return (int) r;

        } catch (Exception ex) {
            if (!formula.equals(lastBad)) {
                Main.LOGGER.warn(
                        "Invalid experienceBottleEquation '{}'. Reverting.",
                        formula
                );
                lastBad = formula;
            }
            return eval(DEFAULT_EQ, world);
        }
    }

    // --- Injection ---

    @ModifyVariable(
            method = "onCollision",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private int modifyXP(int original, @Local ServerWorld serverWorld) {
        return eval(
                Main.config.get("experience_bottle.xpDropEquation", DEFAULT_EQ),
                serverWorld
        );
    }
}