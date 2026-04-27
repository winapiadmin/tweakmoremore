package net.winapiadmin.tweakmoremore.mixin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.block.BlockState;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.explosion.*;
import net.minecraft.command.argument.EntityArgumentType;
import net.winapiadmin.tweakmoremore.Main;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionImplMixin {

    @Shadow @Final private ServerWorld world;
    @Shadow @Final private Entity entity;
    @Shadow @Mutable @Final private float power;
    @Shadow @Final private Vec3d pos;
    @Shadow @Mutable @Final private boolean createFire;
    @Shadow @Mutable @Final private Explosion.DestructionType destructionType;
    @Shadow @Final private DamageSource damageSource;
    @Shadow @Final private ExplosionBehavior behavior;
    @Shadow @Final private Map<PlayerEntity, Vec3d> knockbackByPlayer;

    @Unique private String name;
    @Unique private Map<BlockPos, Float> cachedField;
    @Unique
    private static Collection<? extends Entity> parseSelector(
            ServerCommandSource source,
            String selector
    ) throws CommandSyntaxException {
        if (selector.isEmpty()) return List.of();
        StringReader reader = new StringReader(selector);

        EntityArgumentType arg = EntityArgumentType.entities();
        EntitySelector selectorObj = arg.parse(reader);

        return selectorObj.getEntities(source);
    }
    /* ------------------------------------------------------------ */
    /* INIT                                                          */
    /* ------------------------------------------------------------ */

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterInit(ServerWorld world, @Nullable Entity entity,
                           @Nullable DamageSource damageSource,
                           @Nullable ExplosionBehavior behavior,
                           Vec3d pos, float power, boolean createFire,
                           Explosion.DestructionType destructionType,
                           CallbackInfo ci) {

        if (entity != null) {
            name = Registries.ENTITY_TYPE.getId(entity.getType()).toShortString();
        } else if (damageSource != null) {

            var type = damageSource.getTypeRegistryEntry();

            if (type.matchesKey(DamageTypes.MACE_SMASH))
                return;

            if (type.matchesKey(DamageTypes.BAD_RESPAWN_POINT)) {
                if (world.getRegistryKey() == ServerWorld.OVERWORLD)
                    name = "respawn_anchor";
                else
                    name = "bed";
            }
        }

        if (name == null) return;

        this.power = Main.config.get(name + "_explosionPower", power);
        this.createFire = Main.config.get(name + "_createFire", createFire);

        try {
            String stored = Main.config.get(name + "_destroyBlocks",
                    destructionType.name());
            this.destructionType = Explosion.DestructionType.valueOf(stored);
        } catch (Exception ignored) {}
    }

    /* ------------------------------------------------------------ */
    /* CONFIG HELPERS                                                */
    /* ------------------------------------------------------------ */

    @Unique
    private String damageMode() {
        return Main.config.get(name + "_calcDamageMode", "vanilla");
    }

    @Unique
    private String blockMode() {
        return Main.config.get(name + "_destroyBlocksLogic", "vanilla");
    }

    @Unique
    private String damageEntitiesMode() {
        return Main.config.get(name + "_damageEntitiesLogic", "vanilla");
    }

    @Unique
    private boolean damageEntitiesEnabled() {
        return Main.config.get(name + "_damageEntities", true);
    }

    /* ------------------------------------------------------------ */
    /* DAMAGE CALCULATION                                            */
    /* ------------------------------------------------------------ */

    @Unique
    private float damageCalculation(Vec3d pos, Entity entity) {

        if (name == null)
            return ExplosionImpl.calculateReceivedDamage(pos, entity);

        return switch (damageMode()) {

            case "off" -> 0f;

            case "fixed" ->
                    Main.config.get(name + "_fixedDamage", 5.0F);

            case "new" -> {

                float initialEnergy = power * 2f;
                Map<BlockPos, Float> field = getBlastField(initialEnergy);

                yield calculateReceivedDamage(entity, field, initialEnergy);
            }

            default -> ExplosionImpl.calculateReceivedDamage(pos, entity);
        };
    }

    @Unique
    private Map<BlockPos, Float> getBlastField(float initialEnergy) {

        if (cachedField == null)
            cachedField = computeBlastField(initialEnergy);

        return cachedField;
    }

    /* ------------------------------------------------------------ */
    /* ENTITY EXPOSURE                                               */
    /* ------------------------------------------------------------ */

    @Unique
    private static float calculateReceivedDamage(Entity entity,
                                                 Map<BlockPos, Float> energyMap,
                                                 float initialEnergy) {

        Box box = entity.getBoundingBox();

        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.floor(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.floor(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.floor(box.maxZ);

        float energySum = 0f;
        int samples = 0;

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++) {

                    Float e = energyMap.get(new BlockPos(x, y, z));

                    if (e != null)
                        energySum += e;

                    samples++;
                }

        if (samples == 0) return 0f;

        float avgEnergy = energySum / samples;

        return MathHelper.clamp(avgEnergy / initialEnergy, 0f, 1f);
    }

    /* ------------------------------------------------------------ */
    /* BLAST PROPAGATION (BFS)                                       */
    /* ------------------------------------------------------------ */

    @Unique
    private Map<BlockPos, Float> computeBlastField(float initialEnergy) {

        Map<BlockPos, Float> result = new HashMap<>();

        Long2FloatOpenHashMap energy = new Long2FloatOpenHashMap();
        energy.defaultReturnValue(-1f);

        ArrayDeque<Long> queue = new ArrayDeque<>();

        BlockPos startPos = BlockPos.ofFloored(pos);
        long start = startPos.asLong();

        energy.put(start, initialEnergy);
        queue.add(start);

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        int maxRadius = (int)Math.ceil(initialEnergy);
        int maxRadiusSq = maxRadius * maxRadius;

        while (!queue.isEmpty()) {

            long packed = queue.poll();
            float currentEnergy = energy.get(packed);

            if (currentEnergy <= 0.1f)
                continue;

            BlockPos p = BlockPos.fromLong(packed);

            for (Direction dir : Direction.values()) {

                mutable.set(p).move(dir);

                if (mutable.getSquaredDistance(startPos) > maxRadiusSq)
                    continue;

                BlockState state = world.getBlockState(mutable);

                float resistance =
                        state.isAir() ? 0f :
                                state.getBlock().getBlastResistance();

                float nextEnergy =
                        currentEnergy - resistance * 0.3f - 0.225f;

                if (nextEnergy <= 0f)
                    continue;

                long next = mutable.asLong();
                float prev = energy.get(next);

                if (prev >= nextEnergy)
                    continue;

                energy.put(next, nextEnergy);
                queue.add(next);
            }
        }

        for (Long2FloatMap.Entry entry : energy.long2FloatEntrySet())
            result.put(BlockPos.fromLong(entry.getLongKey()),
                    entry.getFloatValue());

        return result;
    }

    /* ------------------------------------------------------------ */
    /* ENTITY DAMAGE LOOP                                            */
    /* ------------------------------------------------------------ */

    @Unique
    private void damageEntities() throws CommandSyntaxException {

        if (name == null) return;
        if (!damageEntitiesEnabled()) return;
        if (power < 1.0E-5F) return;

        Explosion self = (Explosion) this;

        float radius = power * 2f;

        Box box = new Box(
                pos.x - radius - 1,
                pos.y - radius - 1,
                pos.z - radius - 1,
                pos.x + radius + 1,
                pos.y + radius + 1,
                pos.z + radius + 1
        );
        String includeEntitiesSelector=Main.config.get(name+"_damageEntitiesInclude", "@e");
        String excludeEntitiesSelector=Main.config.get(name+"_damageEntitiesExclude", "");
        ServerCommandSource source= Objects.requireNonNull(world.getServer()).getCommandSource();
        Set<Entity> targets = new HashSet<>(world.getOtherEntities(this.entity, box));
        try{
            targets.addAll(parseSelector(source, includeEntitiesSelector));
        }
        catch(CommandSyntaxException e){
            Main.LOGGER.warn("{}_damageEntitiesInclude option is illegal {}", name, e);
            targets.addAll(parseSelector(source, "@e"));
        }
        try{
            parseSelector(source, excludeEntitiesSelector).forEach(targets::remove);
        }
        catch(CommandSyntaxException e){
            Main.LOGGER.warn("{}_damageEntitiesExclude option is illegal {}", name, e);
        }
        for (Entity entity : targets) {
            if (entity.isImmuneToExplosion(self))
                continue;

            double distance =
                    Math.sqrt(entity.squaredDistanceTo(pos)) / radius;

            if (distance > 1.0)
                continue;

            Vec3d eye = entity instanceof TntEntity
                    ? entity.getEntityPos()
                    : entity.getEyePos();

            Vec3d dir = eye.subtract(pos).normalize();

            boolean damage = behavior.shouldDamage(self, entity);
            float knockbackMod = behavior.getKnockbackModifier(entity);

            float exposure =
                    (!damage && knockbackMod == 0)
                            ? 0
                            : damageCalculation(pos, entity);

            if (damage)
                entity.damage(world, damageSource,
                        behavior.calculateDamage(self, entity, exposure));

            double resist =
                    entity instanceof LivingEntity living
                            ? living.getAttributeValue(
                            EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE)
                            : 0;

            double strength =
                    (1 - distance) * exposure * knockbackMod * (1 - resist);

            Vec3d velocity = dir.multiply(strength);

            entity.addVelocity(velocity);

            if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE)
                    && entity instanceof ProjectileEntity projectile) {

                projectile.setOwner(damageSource.getAttacker());

            } else if (entity instanceof PlayerEntity player
                    && !player.isSpectator()
                    && (!player.isCreative()
                    || !player.getAbilities().flying)) {

                knockbackByPlayer.put(player, velocity);
            }

            entity.onExplodedBy(this.entity);
        }
    }


    /* ------------------------------------------------------------ */
    /* FIBONACCI SPHERE                                              */
    /* ------------------------------------------------------------ */

    @Unique
    private static Vec3d fibonacciDirection(int i, int n) {

        double phi = Math.PI * (3 - Math.sqrt(5));

        double y = 1 - (i / (double)(n - 1)) * 2;
        double radius = Math.sqrt(1 - y * y);

        double theta = phi * i;

        double x = Math.cos(theta) * radius;
        double z = Math.sin(theta) * radius;

        return new Vec3d(x, y, z);
    }
    @Redirect(method="explode()I", at=@At(value="INVOKE", target="Lnet/minecraft/world/explosion/ExplosionImpl;damageEntities()V"))
    private void damageEntities_(ExplosionImpl instance) throws CommandSyntaxException {
        switch(damageEntitiesMode()){
            case "off" -> {}
            case "new" -> damageEntities();
            default -> instance.damageEntities();
        }
    }
    /* ------------------------------------------------------------ */
    /* BLOCK DESTRUCTION                                             */
    /* ------------------------------------------------------------ */
    @Redirect(method="explode()I", at=@At(value="INVOKE", target="Lnet/minecraft/world/explosion/ExplosionImpl;getBlocksToDestroy()Ljava/util/List;"))
    private List<BlockPos> getBlocksToDestroy_(ExplosionImpl instance){
        return switch (blockMode()) {

            case "off" -> new ObjectArrayList<>();

            case "new" -> {

                Set<BlockPos> set = new HashSet<>();

                int rays = 2048;
                net.minecraft.util.math.random.Random random = world.random;

                for (int i = 0; i < rays; i++) {

                    Vec3d dir = fibonacciDirection(i, rays);

                    /* small direction jitter */
                    dir = dir.add(
                            (random.nextDouble() - 0.5) * 0.04,
                            (random.nextDouble() - 0.5) * 0.04,
                            (random.nextDouble() - 0.5) * 0.04
                    ).normalize();

                    double dx = dir.x;
                    double dy = dir.y;
                    double dz = dir.z;

                    float energy =
                            power * (0.7F + random.nextFloat() * 0.6F);

                    /* jittered starting position */
                    double x = pos.x + (random.nextDouble() - 0.5) * 0.2;
                    double y = pos.y + (random.nextDouble() - 0.5) * 0.2;
                    double z = pos.z + (random.nextDouble() - 0.5) * 0.2;

                    while (energy > 0f) {

                        BlockPos blockPos = BlockPos.ofFloored(x, y, z);

                        if (!world.isInBuildLimit(blockPos))
                            break;

                        BlockState state = world.getBlockState(blockPos);
                        FluidState fluid = world.getFluidState(blockPos);

                        Optional<Float> resistance =
                                behavior.getBlastResistance(
                                        (Explosion) this,
                                        world, blockPos, state, fluid
                                );

                        if (resistance.isPresent())
                            energy -= (resistance.get() + 0.3F) * 0.3F;

                        if (energy > 0 &&
                                behavior.canDestroyBlock(
                                        (Explosion) this,
                                        world, blockPos, state, energy)) {

                            set.add(blockPos);
                        }

                        energy -= 0.225F;

                        /* randomized step to break grid resonance */
                        double step = 0.28 + random.nextDouble() * 0.05;

                        x += dx * step;
                        y += dy * step;
                        z += dz * step;
                    }
                }

                yield new ObjectArrayList<>(set);
            }

            default -> instance.getBlocksToDestroy();
        };
    }
}