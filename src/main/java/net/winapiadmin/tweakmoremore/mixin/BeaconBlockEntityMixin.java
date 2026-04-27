package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;
import net.winapiadmin.tweakmoremore.Main;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {

    @Unique private static final String DEFAULT_RADIUS_EQ = "beaconLevel * 20 + 20";
    @Unique private static final String DEFAULT_DURATION_EQ = "(9 + beaconLevel * 4) * 20";
    @Unique private static final String DEFAULT_AMPLIFIER_EQ = "1";

    // cache compiled expressions
    @Unique
    private static final Map<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    // track bad formulas so we don’t spam logs
    @Unique private static final Map<String, String> LAST_BAD = new ConcurrentHashMap<>();

    @Unique
    private static Expression getExpression(String formula) {
        return EXPRESSION_CACHE.computeIfAbsent(formula, f ->
                new ExpressionBuilder(f)
                        .variable("beaconLevel")
                        .build()
        );
    }

    @Unique
    private static double evaluate(
            String key,
            String formula,
            int beaconLevel,
            double fallback
    ) {
        try {
            Expression expr = getExpression(formula)
                    .setVariable("beaconLevel", beaconLevel);

            double result = expr.evaluate();

            if (!Double.isFinite(result)) throw new RuntimeException();

            return result;

        } catch (Exception e) {
            String last = LAST_BAD.get(key);
            if (!formula.equals(last)) {
                Main.LOGGER.warn("Invalid {} '{}'. Reverting to default.", key, formula);
                LAST_BAD.put(key, formula);
            }
            return fallback;
        }
    }

    /**
     * @author you
     * @reason configurable beacon scaling without burning the server alive
     */
    @Overwrite
    private static void applyPlayerEffects(
            World world,
            BlockPos pos,
            int beaconLevel,
            @Nullable RegistryEntry<StatusEffect> primaryEffect,
            @Nullable RegistryEntry<StatusEffect> secondaryEffect
    ) {
        if (world.isClient() || primaryEffect == null) return;

        // ----- Radius -----
        double defaultRadius = beaconLevel * 20 + 20;
        double radius = clamp(
                evaluate(
                        "beacon.radius",
                        Main.config.get("beacon.radius", DEFAULT_RADIUS_EQ),
                        beaconLevel,
                        defaultRadius
                ),
                0, 2E10
        );

        // ----- Duration -----
        int defaultDuration = (9 + beaconLevel * 4) * 20;
        int duration = (int) clamp(
                evaluate(
                        "beacon.duration",
                        Main.config.get("beacon.duration", DEFAULT_DURATION_EQ),
                        beaconLevel,
                        defaultDuration
                ),
                0, 20 * 24 * 60 * 60
        );

        // ----- Amplifier -----
        int amplifier = 0;
        if (beaconLevel >= 4 && Objects.equals(primaryEffect, secondaryEffect)) {
            String name_="beacon.amplifier."+Registries.STATUS_EFFECT.getId(primaryEffect.value()).toShortString();
            amplifier = (int) evaluate(
                    name_,
                    Main.config.get(name_, DEFAULT_AMPLIFIER_EQ),
                    beaconLevel,
                    1
            );
        }

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        double radiusSq = radius * radius;

        List<? extends PlayerEntity> players = world.getPlayers();

        for (PlayerEntity player : players) {
            if (player.isSpectator()) continue;

            if (player.squaredDistanceTo(cx, cy, cz) <= radiusSq) {

                player.addStatusEffect(
                        new StatusEffectInstance(primaryEffect, duration, amplifier, true, true)
                );

                if (beaconLevel >= 4
                        && secondaryEffect != null
                        && !Objects.equals(primaryEffect, secondaryEffect)) {

                    player.addStatusEffect(
                            new StatusEffectInstance(secondaryEffect, duration, 0, true, true)
                    );
                }
            }
        }
    }
}