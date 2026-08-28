# Configuration Guide

This guide explains how to configure tweakmoremore through the JSON config file.

## Config File Location

- **Singleplayer**: `.minecraft/config/tweakmoremore.json`
- **Multiplayer**: World-specific config at `<world>/tweakmoremore.json` (overrides global config)

## Config File Format

```json
{
  "DataVersion": 2,
  "mob_cap.chunk_area": 289,
  "item.diamond_sword.maxCountPerStack": 99,
  "block.diamond_ore.modifiers.hardness": 3.0
}
```

## Data Types

| Type | Example |
|------|---------|
| Boolean | `true`, `false` |
| Integer | `64`, `200` |
| Float | `1.5f`, `0.8f` |
| String | `"formula_expression"` |

## Expression Formulas

Some config options accept mathematical expressions using the exp4j library.

### Available Variables

- `experienceLevel` - Player's experience level
- `totalExperience` - Player's total experience points
- `experienceProgress` - Current XP progress (0.0-1.0)
- `beaconLevel` - Beacon pyramid level (0-4)
- `amount` - Damage amount
- `n` - Number of slots (for equipment damage)

### Functions

- `min(a, b)` - Minimum of a and b
- `max(a, b)` - Maximum of a and b
- `randInt(min, max)` - Random integer in range
- `random` - Random double (0.0-1.0)
- `random(min, max)` - Random double in range

### Examples

```json
{
  "player.xp.dropFormula": "min(experienceLevel*7,100)",
  "experience_bottle.xpDropEquation": "3+randInt(0,5)+randInt(0,5)",
  "beacon.radius": "beaconLevel * 20 + 20",
  "beacon.duration": "(9 + beaconLevel * 4) * 20"
}
```

## Dynamic Block Properties

Properties can be customized per-block using the naming pattern:

```
block.<block_name>.modifiers.<property>
```

### Available Block Properties

| Property | Type |
|----------|------|
| `collidable` | boolean |
| `resistance` | float |
| `hardness` | float |
| `toolRequired` | boolean |
| `randomTicks` | boolean |
| `slipperiness` | float |
| `velocityMultiplier` | float |
| `jumpVelocityMultiplier` | float |
| `opaque` | boolean |
| `isAir` | boolean |
| `burnable` | boolean |
| `liquid` | boolean |
| `forceNotSolid` | boolean |
| `forceSolid` | boolean |
| `pistonBehavior` | string |
| `dynamicBounds` | boolean |

### Piston Behavior Values

- `NORMAL`
- `DESTROY`
- `PUSH_ONLY`
- `NULL`

## Dynamic Item Properties

Properties can be customized per-item using the naming pattern:

```
item.<item_name>.<property>
```

### Available Item Properties

| Property | Type |
|----------|------|
| `maxCountPerStack` | int |
| `stackSize` | int |
| `useCooldown` | float |
| `damageMultiplier` | float |

### Damage Multiplier Targeting

```
item.<item_name>.damageMultiplerPlayersInclude=@p,@a
item.<item_name>.damageMultiplerPlayersExclude=@a
```

Entity selectors:
- `@p` - Nearest player
- `@a` - All players
- `@e` - All entities
- `@s` - Self

## Food Properties

```
food.<item_name>.<property>
```

| Property | Type |
|----------|------|
| `nutrition` | int |
| `saturation` | float |
| `eattime` | float |
| `alwaysEdible` | boolean |

## Equipment Drop Chances

```
equipment.drop_chance=0.0
```

Sets the drop chance multiplier for all equipment (0.0 = never drop, 1.0 = normal chance).

## XP Block Drops

Customize XP drops from blocks like ores:

```
xpDroppingBlock.<block_name>.minExp=1
xpDroppingBlock.<block_name>.maxExp=5
```

## Snowball Damage

Configure snowball damage per entity type:

```
snowball.damage_to_<entity_type>=0
```

Example: `snowball.damage_to_player=1`

## Mob Spawner Settings

```
mob_spawner.min_spawn_delay=200
mob_spawner.max_spawn_delay=800
mob_spawner.spawn_count=4
mob_spawner.max_nearby_entities=6
mob_spawner.required_player_range=16
mob_spawner.spawn_range=4
```

## Potion Effect Durations

```
potion.<potion_name>.modifiers.<effect_name>.duration=<ticks>
```

Example: `potion.mundane.modifiers.minecraft:strength.duration=900`