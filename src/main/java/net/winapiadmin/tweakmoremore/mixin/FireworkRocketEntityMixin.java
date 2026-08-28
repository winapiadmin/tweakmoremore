package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {

    @Shadow private int lifeTime;

    @Unique private static final String DEFAULT_EQ = "10*flightDuration+randInt(0,6)+randInt(0,7)";
    @Unique private static String lastBad = null;

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
                int min = (int)args[0];
                int max = (int)args[1];
                if (max <= min)
                    return min;
                return world.random.nextInt(max - min) + min;
            }
        };
    }
    // --- Eval ---

    @Unique
    private static int eval(String formula, int flightDuration, ServerWorld world) {
        try {
            Expression e = new ExpressionBuilder(formula)
                               .function(random0(world))
                               .function(random2(world))
                               .function(randInt(world)) // randInt(a,b)
                               .variable("flightDuration")
                               .build()
                               .setVariable("flightDuration", flightDuration);

            double r = e.evaluate();
            if (!Double.isFinite(r))
                throw new RuntimeException();

            return (int)r;

        } catch (Exception ex) {
            if (!formula.equals(lastBad)) {
                Main.LOGGER.warn("Invalid item.fireworkRocket.flightTime '{}'. Reverting.", formula);
                lastBad = formula;
            }
            return eval(DEFAULT_EQ, flightDuration, world);
        }
    }

    // --- Injection ---
    // <- lifetime <- eq [duration=1+flightDuration]
    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    private void onInit(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        int flightDuration = 1;
        FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
        if (fireworksComponent != null) {
            flightDuration += fireworksComponent.flightDuration();
        }
        this.lifeTime = eval(Main.config.get("item.firework_rocket.flightTime", DEFAULT_EQ), flightDuration, (ServerWorld)world);
    }
}