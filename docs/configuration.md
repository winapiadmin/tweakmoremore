# Configuration Guide

This guide explains how to configure tweakmoremore through the JSON config file.

## Config File Location

- **Singleplayer**: `.minecraft/config/tweakmoremore.json`
- **Multiplayer**: World-specific config at `<world>/tweakmoremore.json` (overrides global config)

## Config File Format

```json
{
  "DataVersion": 2,
  "values": {
    "mob_cap.chunk_area": 289,
    "maceSmashMinFallDistance": 1.5,
    "attribute.max_health.max": 1024.0
  }
}
```

## Data Types

| Type | Example |
|------|---------|
| Boolean | `true`, `false` |
| Integer | `64`, `200` |
| Float | `1.5`, `0.8` |
| Double | `1.5`, `1024.0` |
| String | `"formula_expression"` |

## Expression Formulas

Some config options accept mathematical expressions using the exp4j library.

### Available Variables

- `experienceLevel` — Player's experience level
- `totalExperience` — Player's total experience points
- `experienceProgress` — Current XP progress (0.0-1.0)
- `beaconLevel` — Beacon pyramid level (0-4)
- `amount` — Damage amount
- `n` — Number of slots (for equipment damage)

### Functions

- `min(a, b)` — Minimum of a and b
- `max(a, b)` — Maximum of a and b
- `randInt(min, max)` — Random integer in range
- `random` — Random double (0.0-1.0)
- `random(min, max)` — Random double in range

### Examples

```json
{
  "values": {
    "player.xp.dropFormula": "min(experienceLevel*7,100)",
    "experience_bottle.xpDropEquation": "3+randInt(0,5)+randInt(0,5)",
    "beacon.radius": "beaconLevel * 20 + 20",
    "beacon.duration": "(9 + beaconLevel * 4) * 20"
  }
}
```

---

## Dynamic Block Properties

Properties can be customized per-block using the naming pattern:

```json
{
  "values": {
    "block.<block_name>.modifiers.<property>": <value>
  }
}
```

```json
{
  "values": {
    "block.<block_name>.modifiers.collidable": true,
    "block.<block_name>.modifiers.resistance": 6.0,
    "block.<block_name>.modifiers.hardness": 5.0,
    "block.<block_name>.modifiers.toolRequired": false,
    "block.<block_name>.modifiers.randomTicks": false,
    "block.<block_name>.modifiers.slipperiness": 0.6,
    "block.<block_name>.modifiers.velocityMultiplier": 1.0,
    "block.<block_name>.modifiers.jumpVelocityMultiplier": 1.0,
    "block.<block_name>.modifiers.pistonBehavior": "NORMAL",
    "block.<block_name>.modifiers.dynamicBounds": false,
    "block.<block_name>.modifiers.opaque": true,
    "block.<block_name>.modifiers.isAir": false,
    "block.<block_name>.modifiers.burnable": false,
    "block.<block_name>.modifiers.liquid": false,
    "block.<block_name>.modifiers.forceNotSolid": false,
    "block.<block_name>.modifiers.forceSolid": false
  }
}
```

Piston behavior values: `NORMAL`, `DESTROY`, `PUSH_ONLY`, `BLOCK`, `IGNORE`.

## Dynamic Item Properties

```json
{
  "values": {
    "item.<item_name>.maxCountPerStack": 64,
    "item.<item_name>.stackSize": 64,
    "item.<item_name>.useCooldown": 0.0,
    "item.<item_name>.damageMultiplier": 1.0,
    "item.<item_name>.truncateItemCountShown": true,
    "item.<any>.maxCountPerStack": 64,
    "item.<item_name>.damageMultiplerPlayersInclude": "",
    "item.<item_name>.damageMultiplerPlayersExclude": "@a",
    "item.despawn_age": 6000,
    "item.<item_name>_stackSize": 64
  }
}
```

Entity selectors: `@p` (nearest), `@a` (all), `@e` (all entities), `@s` (self).

## Food Properties

```json
{
  "values": {
    "food.<item_name>.nutrition": 3,
    "food.<item_name>.saturation": 0.5,
    "food.<item_name>.eattime": 32,
    "food.<item_name>.alwaysEdible": false
  }
}
```

## Mob Caps

Per-spawn-group overrides (e.g. `monster`, `creature`, `ambient`, `water_creature`, `misc`):

```json
{
  "values": {
    "mob_cap.chunk_area": 289,
    "mob_cap.monster": 70,
    "mob_cap.creature": 10
  }
}
```

---

## Beacon

```json
{
  "values": {
    "beacon.radius": "beaconLevel * 20 + 20",
    "beacon.duration": "(9 + beaconLevel * 4) * 20",
    "beacon.amplifier.<effect_name>": 0
  }
}
```

The `beacon.amplifier.<effect_name>` key sets the amplifier for a specific status effect when the beacon is at level 4. Use the effect's registry name (e.g. `speed`, `haste`, `regeneration`).

## Piston

```json
{
  "values": {
    "piston.push_limit": 12
  }
}
```

---

## Enchantments

```json
{
  "values": {
    "enchantment.codec.weight": 1024,
    "enchantment.codec.max_level": 255
  }
}
```

---

## Firework Rockets

```json
{
  "values": {
    "item.firework_rocket.flightTime": "flightDuration"
  }
}
```

Accepts an expression formula. Variables: `flightDuration` (the item's flight duration value).

---

## Entity Settings

### Piglin AI

```json
{
  "values": {
    "entity.piglin.admireGoldPriority": 10,
    "entity.piglin.findGoldRadius": 8,
    "entity.piglin.findGoldSpeedModifier": 1.0,
    "entity.piglin.findGoldTimeTicks": 120,
    "entity.piglin.refuseTradeCooldownTicks": 20,
    "entity.piglin.tradeTime": 120
  }
}
```

### Snow Golem

```json
{
  "values": {
    "snow_golem.shoot_cooldown": 20,
    "snow_golem.shoot_range": 10.0,
    "snow_golem.hurt_by_water": true
  }
}
```

### Damage Equipment Behavior

Per-damage-type expressions for equipment durability loss:

```json
{
  "values": {
    "entity.damageEquipmentBehavior<damage_type>.damageExpression": "max(1.0,amount/4.0)"
  }
}
```

Variables: `n` (number of slots), `amount` (damage amount).

---

## Player

```json
{
  "values": {
    "entity.showFallDist": true,
    "player.showDamageInfo": false,
    "player.xp.dropFormula": "experienceLevel * 7"
  }
}
```

---

## Sculk Blocks

```json
{
  "values": {
    "sculk_minExp": 1,
    "sculk_maxExp": 10
  }
}
```

---

## Villager Settings

```json
{
  "values": {
    "villager.item_pickup_range": 3,
    "villager.item_sense_horizontal": 32,
    "villager.item_sense_vertical": 16
  }
}
```

---

## Snowball Damage

```json
{
  "values": {
    "snowball.damage_to_<entity_type>": 0
  }
}
```

## Potion Effect Durations

```json
{
  "values": {
    "potion.<potion_name>.modifiers.<effect_name>.duration": 900
  }
}
```

---

## Brewing Stands

```json
{
  "values": {
    "brewing_stands.brewTime": 400,
    "brewing_stands.brewFuel": 20,
    "brewing_stands.tick": true
  }
}
```

## Composter

```json
{
  "values": {
    "composterCompostDelay": 20
  }
}
```

```json
{
  "values": {
    "maceSmashMinFallDistance": 1.5,
    "maceSmashRequiresNotGliding": true,
    "maceSmashFallThresholdLow": 3.0,
    "maceSmashFallThresholdHigh": 8.0,
    "maceSmashDamagePerBlockLow": 4.0,
    "maceSmashDamagePerBlockMid": 2.0,
    "maceSmashDamagePerBlockHigh": 1.0,
    "maceSmashKnockbackRange": 3.5,
    "maceSmashKnockbackPower": 0.7,
    "maceSmashHeavyKnockbackMultiplier": 2.0
  }
}
```

### Damage Formula

Bonus damage is computed as a piecewise linear function of fall distance:

| Fall Distance | Damage |
|---------------|--------|
| ≤ `fallThresholdLow` | `damagePerBlockLow × fallDistance` |
| ≤ `fallThresholdHigh` | `damagePerBlockLow × thresholdLow + damagePerBlockMid × (fall - thresholdLow)` |
| > `fallThresholdHigh` | above + `damagePerBlockHigh × (fall - thresholdHigh)` |

Density enchantment bonus is then added on top.

---

## Max Health Limit

The vanilla `max_health` attribute is hard-capped at 1024. This setting raises that cap.

```json
{
  "values": {
    "attribute.max_health.max": 1024.0
  }
}
```

Set higher to allow attribute values beyond 1024 via `/attribute ... base set ...` or modifiers.

Example: to allow 100k HP, set `attribute.max_health.max=100000`.

---

## Explosions

Per-explosive settings use the naming pattern `explosive.<name>_<property>` where `<name>` is the explosion source (e.g. `tnt`, `bed`, `respawn_anchor`, `tnt_minecart`).

```json
{
  "values": {
    "explosive.<name>_explosionPower": 4.0,
    "explosive.<name>_createFire": false,
    "explosive.<name>_destroyBlocks": "vanilla",
    "explosive.<name>_destroyBlocksLogic": "vanilla",
    "explosive.<name>_damageEntities": true,
    "explosive.<name>_damageEntitiesLogic": "vanilla",
    "explosive.<name>_fixedDamage": 5.0,
    "explosive.<name>_calcDamageMode": "vanilla",
    "explosive.<name>_damageEntitiesInclude": "@e",
    "explosive.<name>_damageEntitiesExclude": ""
  }
}
```

Special: `explosive.tnt_minecart.fixedPower=bool` — force TNT minecart explosion power from config instead of rail speed.

---

## Mob Spawner

```json
{
  "values": {
    "mob_spawner.min_spawn_delay": 200,
    "mob_spawner.max_spawn_delay": 800,
    "mob_spawner.spawn_count": 4,
    "mob_spawner.max_nearby_entities": 6,
    "mob_spawner.required_player_range": 16,
    "mob_spawner.spawn_range": 4
  }
}
```

---

## Equipment Drop Chances

```json
{
  "values": {
    "equipment.drop_chance": 1.0
  }
}
```

## XP Block Drops

```json
{
  "values": {
    "xpDroppingBlock.<block_name>.minExp": 1,
    "xpDroppingBlock.<block_name>.maxExp": 5
  }
}
```

## Lava / Water Fluid

```json
{
  "values": {
    "lavaFastPlayTickRate": 10,
    "lavaNonFastPlayTickRate": 30,
    "lavaFastPlayLevelDecreasePerBlock": 1,
    "lavaNonFastPlayLevelDecreasePerBlock": 2,
    "lavaFastPlayMaxFlowDist": 4,
    "lavaNonFastPlayMaxFlowDist": 2,
    "lavaRandomTick": true,
    "stillLavaFluidLevel": 8,
    "stillWaterFluidLevel": 8,
    "waterTickRate": 5,
    "waterLevelDecreasePerBlock": 1,
    "waterMaxFlowDist": 4
  }
}
```

## Hunger / Regen

```json
{
  "values": {
    "regen.fast_interval": 10,
    "regen.slow_interval": 80,
    "regen.starvation_interval": 80
  }
}
```

## Damage

```json
{
  "values": {
    "damage.invulnerability_ticks": 20,
    "damage.hurt_animation_ticks": 10
  }
}
```

## World Ticks

```json
{
  "values": {
    "tickWeather": true,
    "tickTime": true,
    "tick_raids": true,
    "sleep_never_skip": false,
    "tickBlockEntities": true,
    "tickWorldBorder": true
  }
}
```

---

## Bug Fixes

Each bugfix is gated by a boolean toggle (default `false` = off). Set to `true` to enable.

| Key | Description |
|-----|-------------|
| `bugfix.BlockPos.enableOptimizedIterate` | Use optimized iteration over block positions |
| `bugfix.BannedIpList.IPnormalization` | Normalize embedded IPv4 addresses in ban list |
| `bugfix.BooleanModifier.correctXnorOp` | Fix incorrect XNOR boolean modifier behavior |
| `bugfix.ChaseServer.copyPlayerListOnTeleport` | Copy player list before teleport to prevent concurrent modification |
| `bugfix.Entity.clampAge` | Clamp entity age to valid range on load |
| `bugfix.Entity.correctPistonMovement` | Fix piston movement calculations |
| `bugfix.Entity.correctPistonVelocity` | Fix entity velocity when pushed by pistons |
| `bugfix.ExecutorSampling.syncPut` | Synchronize activeExecutors map on put |
| `bugfix.ExecutorSampling.syncKeySet` | Synchronize activeExecutors keySet view |
| `bugfix.MobEntity.clampAmbientSoundChance` | Clamp ambient sound chance to valid range |
| `bugfix.NbtByteArray.assertLength` | Validate NBT byte array lengths |
| `bugfix.NbtIntArray.assertLength` | Validate NBT int array lengths |
| `bugfix.NbtLongArray.assertLength` | Validate NBT long array lengths |
| `bugfix.NbtType.OfFixedType.assertLength` | Validate fixed-size NBT type array counts |
| `bugfix.PacketApplyBatcher.limit` | Max queued packets before rejecting (default: `Integer.MAX_VALUE`) |
| `bugfix.PacketByteBuf.assertSize` | Validate collection sizes when reading packets |
| `bugfix.PacketInflater.assertLength` | Validate uncompressed packet size |
| `bugfix.RconClient.resetBufferedStreamProperly` | Reset buffered stream on reconnect |
| `bugfix.RconClient.setTimeout` | Apply socket timeout on RCON connect |
| `bugfix.ServerPlayerEntity.savePlayerDataOnDeath` | Save player data immediately on death |
| `bugfix.block.retainBlockEntityComponents` | Retain block entity components in drops |
| `bugfix.experienceOrbNoOverflow` | Prevent XP orb count overflow |
| `bugfix.minecart.correctPushAwayFromMinecart` | Fix minecart collision push direction |
| `bugfix.tnt_minecart.skipRailDetonation` | Skip TNT minecart rail detonation |
| `bugfix.PathNode.properHash` | Fix path node hash calculation |
| `bugfix.RegistryKey.accurateHashCode` | Fix RegistryKey hashCode accuracy |
| `bugfix.RegistryKey.equalWithObjectOverload` | Fix RegistryKey equals with Object overload |
| `bugfix.RconListener.useCOWArrayList` | Use copy-on-write ArrayList for RCON listener |
| `bugfix.ServerNetworkIo.DelayingChannelInboundHandler.useConcurrentMap` | Use ConcurrentHashMap for channel handler |
| `bugfix.Vec2f.accurateHashCode` | Fix Vec2f hashCode accuracy |
| `bugfix.Vec2f.equalWithObjectOverload` | Fix Vec2f equals with Object overload |

---

## Server Player Toggles

Toggle individual player tick behaviors:

```json
{
  "values": {
    "tickPlayer": true,
    "tickFallStartPos": true,
    "tickSculkShriekerWarningManager": true,
    "tickVehicleInLavaRiding": true
  }
}
```

---

## Miscellaneous

```json
{
  "values": {
    "allowPlaceSameFluidAndBlock": true,
    "attribute_swap_fix.enabled": false,
    "commands.forceRandomTick": false,
    "equipment.drop_chance": 1.0
  }
}
```
