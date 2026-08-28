package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.block.spawner.MobSpawnerLogic;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerLogic.class)
public abstract class MobSpawnerLogicMixin {

    @Shadow private int minSpawnDelay;
    @Shadow private int maxSpawnDelay;
    @Shadow private int spawnCount;
    @Shadow private int maxNearbyEntities;
    @Shadow private int requiredPlayerRange;
    @Shadow private int spawnRange;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void overrideDefaults(CallbackInfo ci) {
        this.minSpawnDelay = Main.config.get("mob_spawner.min_spawn_delay", this.minSpawnDelay);
        this.maxSpawnDelay = Main.config.get("mob_spawner.max_spawn_delay", this.maxSpawnDelay);
        this.spawnCount = Main.config.get("mob_spawner.spawn_count", this.spawnCount);
        this.maxNearbyEntities = Main.config.get("mob_spawner.max_nearby_entities", this.maxNearbyEntities);
        this.requiredPlayerRange = Main.config.get("mob_spawner.required_player_range", this.requiredPlayerRange);
        this.spawnRange = Main.config.get("mob_spawner.spawn_range", this.spawnRange);
    }

    @ModifyConstant(method = "readData", constant = @Constant(intValue = 200, ordinal = 0))
    private int overrideMinSpawnDelay(int original) {
        return Main.config.get("mob_spawner.min_spawn_delay", original);
    }

    @ModifyConstant(method = "readData", constant = @Constant(intValue = 800))
    private int overrideMaxSpawnDelay(int original) {
        return Main.config.get("mob_spawner.max_spawn_delay", original);
    }

    @ModifyConstant(method = "readData", constant = @Constant(intValue = 4, ordinal = 0))
    private int overrideSpawnCount(int original) {
        return Main.config.get("mob_spawner.spawn_count", original);
    }

    @ModifyConstant(method = "readData", constant = @Constant(intValue = 6))
    private int overrideMaxNearbyEntities(int original) {
        return Main.config.get("mob_spawner.max_nearby_entities", original);
    }

    @ModifyConstant(method = "readData", constant = @Constant(intValue = 16))
    private int overrideRequiredPlayerRange(int original) {
        return Main.config.get("mob_spawner.required_player_range", original);
    }

    @ModifyConstant(method = "readData", constant = @Constant(intValue = 4, ordinal = 1))
    private int overrideSpawnRange(int original) {
        return Main.config.get("mob_spawner.spawn_range", original);
    }
}
