package net.winapiadmin.tweakmoremore.mixin;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.*;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.component.type.*;
import net.winapiadmin.tweakmoremore.ExperienceDroppingBlockAccessor;
import net.winapiadmin.tweakmoremore.Main;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mixin(Registry.class)
public interface RegistryMixin {
    /**
     * Registers {@code entry} to {@code registry} under {@code key}.
     *
     * @return the passed {@code entry}
     */
    @Overwrite
    static <R, T extends R> net.minecraft.registry.entry.RegistryEntry.Reference<T> registerReference(Registry<R> registry, RegistryKey<R> key, T entry) {
        if (registry.equals(Registries.BLOCK) && entry instanceof ExperienceDroppingBlock experienceDroppingBlock){
            setExp(experienceDroppingBlock,key.getValue().toShortString());
        }
        else if (registry.equals(Registries.ITEM) && entry instanceof Item item){
            setConsumeTime(item,key.getValue().toShortString());
            setCooldownTime(item,key.getValue().toShortString());
            setStackSize(item,key.getValue().toShortString());
        }
        else if (registry.equals(Registries.POTION) && entry instanceof Potion pot){
            entry = (T) modifyPotion(pot,key.getValue().toShortString());
        }
        else if (registry.equals(Registries.BLOCK) && entry instanceof Block block){
            modifyBlockSettings(block.getSettings(),key.getValue().toShortString());
        }
        return ((MutableRegistry)registry).add(key, (R)entry, RegistryEntryInfo.DEFAULT);
    }
    /**
     * Registers {@code entry} to {@code registry} under {@code key}.
     *
     * @return the passed {@code entry}
     */
    @Overwrite
    static <V, T extends V> T register(Registry<V> registry, RegistryKey<V> key, T entry) {
        if (registry.equals(Registries.BLOCK) && entry instanceof ExperienceDroppingBlock experienceDroppingBlock){
            setExp(experienceDroppingBlock,key.getValue().toShortString());
        }
        else if (registry.equals(Registries.ITEM) && entry instanceof Item item){
            setConsumeTime(item,key.getValue().toShortString());
            setCooldownTime(item,key.getValue().toShortString());
            setStackSize(item,key.getValue().toShortString());
        }
        else if (registry.equals(Registries.POTION) && entry instanceof Potion pot){
            entry = (T) modifyPotion(pot,key.getValue().toShortString());
        }
        else if (registry.equals(Registries.BLOCK) && entry instanceof Block block){
            modifyBlockSettings(block.getSettings(),key.getValue().toShortString());
        }
        ((MutableRegistry)registry).add(key, entry, RegistryEntryInfo.DEFAULT);
        return entry;
    }
    @Unique
    private static void modifyBlockSettings(AbstractBlock.Settings settings, String name) {
        settings.collidable=Main.config.get("block."+name+".modifiers.collidable", settings.collidable);
        settings.resistance = Main.config.get("block."+name+".modifiers.resistance", settings.resistance);
        settings.hardness = Main.config.get("block."+name+".modifiers.hardness", settings.hardness);
        settings.toolRequired = Main.config.get("block."+name+".modifiers.toolRequired", settings.toolRequired);
        settings.randomTicks = Main.config.get("block."+name+".modifiers.randomTicks", settings.randomTicks);
        settings.slipperiness = Main.config.get("block."+name+".modifiers.slipperiness", settings.slipperiness);
        settings.velocityMultiplier = Main.config.get("block."+name+".modifiers.velocityMultiplier", settings.velocityMultiplier);
        settings.jumpVelocityMultiplier = Main.config.get("block."+name+".modifiers.jumpVelocityMultiplier", settings.jumpVelocityMultiplier);
        settings.opaque = Main.config.get("block."+name+".modifiers.opaque", settings.opaque);
        settings.isAir = Main.config.get("block."+name+".modifiers.isAir", settings.isAir);
        settings.burnable = Main.config.get("block."+name+".modifiers.burnable", settings.burnable);
        settings.liquid = Main.config.get("block."+name+".modifiers.liquid", settings.liquid);
        settings.forceNotSolid = Main.config.get("block."+name+".modifiers.forceNotSolid", settings.forceNotSolid);
        settings.forceSolid = Main.config.get("block."+name+".modifiers.forceSolid", settings.forceSolid);
PistonBehavior current = settings.pistonBehavior;

String defaultPB = (current == null) ? "NULL" : current.name();

String raw = Main.config.get("block." + name + ".modifiers.pistonBehavior", defaultPB);

PistonBehavior parsed;

if (raw.toUpperCase() == "NULL") {
    parsed = null;
} else {
    try {
        parsed = PistonBehavior.valueOf(raw.toUpperCase());
    } catch (IllegalArgumentException e) {
        parsed = current;
        Main.LOGGER.warn(name+".pistonBehavior parse exception: "+e);
    }
}

settings.pistonBehavior = parsed;
        settings.dynamicBounds = Main.config.get("block."+name+".modifiers.dynamicBounds", settings.dynamicBounds);
    }

    @Unique
    private static Potion modifyPotion(Potion potion, String name) {
        List<StatusEffectInstance> effects = potion.getEffects();

        for (StatusEffectInstance inst : effects) {
            String effectName = "";
            Identifier id = Identifier.tryParse(inst.getEffectType().getIdAsString());
            if (id != null)
                effectName = id.toShortString();
            else
                effectName = inst.getTranslationKey();

            inst.duration = Main.config.get(
                    "potion."+name + ".modifiers." + effectName + ".duration", inst.getDuration());
        }

        StatusEffectInstance[] array = effects.toArray(new StatusEffectInstance[0]);
        return new Potion(potion.getBaseName(), array);
    }

    @Unique
    private static  void setConsumeTime(Item item, String id) {
        ComponentMap original = item.getComponents();
        ConsumableComponent consumable = original.get(DataComponentTypes.CONSUMABLE);
        FoodComponent food = original.get(DataComponentTypes.FOOD);

        if (consumable != null) {
            float newSeconds = Main.config.get("food."+id+".eattime", consumable.consumeSeconds());
            consumable = new ConsumableComponent(
                    newSeconds, consumable.useAction(), consumable.sound(),
                    consumable.hasConsumeParticles(), consumable.onConsumeEffects());
        }

        if (food != null) {
            int newNutrition = Main.config.get("food."+id+".nutrition", food.nutrition());
            float newSaturation = Main.config.get("food."+id+".saturation", food.saturation());
            boolean newAlwaysEdible = Main.config.get("food."+id+".alwaysEdible", food.canAlwaysEat());
            FoodComponent.Builder builder = new FoodComponent.Builder()
                    .nutrition(newNutrition)
                    .saturationModifier(newSaturation);

            if (newAlwaysEdible) {
                builder=builder.alwaysEdible();
            }

            food = builder.build();
        }

        // build final map once
        ComponentMap updated = ComponentMap.builder()
                .addAll(original)
                .add(DataComponentTypes.CONSUMABLE, consumable)
                .add(DataComponentTypes.FOOD, food)
                .build();

        ((ItemAccessor) item).setComponents(updated);
    }
@Unique
private static void setCooldownTime(Item item, String id) {
    ComponentMap originalComponents = item.getComponents();
    UseCooldownComponent base = originalComponents.get(DataComponentTypes.USE_COOLDOWN);
    base=base==null?new UseCooldownComponent(0.0f):base;
    Optional<Identifier> cooldownGroup=base.cooldownGroup();
    float newSeconds = Main.config.get("item." + id + ".useCooldown", base.seconds());

    UseCooldownComponent modified = new UseCooldownComponent(
            newSeconds, cooldownGroup);

    ComponentMap updatedMap = ComponentMap.builder()
            .addAll(originalComponents)
            .add(DataComponentTypes.USE_COOLDOWN, modified)
            .build();

    ((ItemAccessor) item).setComponents(updatedMap);
}
    @Unique
    private static void setStackSize(Item item, String id) {
        ComponentMap originalComponents = item.getComponents();
        if (originalComponents.get(DataComponentTypes.MAX_DAMAGE)!=null) {
            Integer maxDamage=originalComponents.get(DataComponentTypes.MAX_DAMAGE);
            ComponentMap updatedMap = ComponentMap.builder()
                    .addAll(originalComponents)
                    .add(DataComponentTypes.MAX_DAMAGE, Main.config.get("tool."+id + ".maxDamage", maxDamage))
                    .build();

            ((ItemAccessor) item).setComponents(updatedMap);
            return;
        }
        Integer stackSize=originalComponents.get(DataComponentTypes.MAX_STACK_SIZE);
        ComponentMap updatedMap = ComponentMap.builder()
                .addAll(originalComponents)
                .add(DataComponentTypes.MAX_STACK_SIZE, Main.config.get("item."+id + ".stackSize", stackSize))
                .build();

        ((ItemAccessor) item).setComponents(updatedMap);
    }

    private static void setExp(ExperienceDroppingBlock xp, String id) {
        IntProvider prov = xp.experienceDropped;
        if (prov instanceof ConstantIntProvider c) {
            prov = UniformIntProvider.create(
                    Main.config.get("xpDroppingBlock."+id + ".minExp", c.getMin()),
                    Main.config.get("xpDroppingBlock."+id + ".maxExp", c.getMax())
            );
        }
        else if (prov instanceof UniformIntProvider u) {
            prov = UniformIntProvider.create(
                    Main.config.get("xpDroppingBlock."+id + ".minExp", u.getMin()),
                    Main.config.get("xpDroppingBlock."+id + ".maxExp", u.getMax())
            );
        }
        ((ExperienceDroppingBlockAccessor) xp).tweakmoremore$setExperienceDropped(prov);
    }
}
