# tweakmoremore

A highly configurable Fabric mod for Minecraft 1.21.11 that allows extensive tweaking of vanilla game mechanics through JSON configuration.

## Features

- **Mob Caps**: Configure spawn caps per mob type and chunk area
- **Experience Drops**: Customizable XP amounts for blocks and bottles via formulas
- **Enchantments**: Adjust weight, max level, costs, and slots
- **Items**: Modify stack sizes, damage multipliers, cooldowns, and food properties
- **Blocks**: Customize hardness, resistance, and other block properties
- **Potions**: Modify potion effect durations
- **Villagers**: Adjust item pickup range and sensing distances
- **Snow Golems**: Configure shoot cooldown, range, and water weakness
- **Piglins**: Customize trade times and gold admiring behavior
- **Brewing Stands**: Change brew time, fuel consumption, and enable/disable
- **Composter**: Adjust compost delay
- **Hunger/Regeneration**: Modify food saturation and regeneration timings
- **Damage**: Configure invulnerability ticks and hurt animation duration
- **World Ticks**: Toggle weather, time, block entities, and world border ticks
- **Force Random Tick**: Command to force random ticks on specific blocks
- **Give Command**: Increased item stack limit in `/give` command

## Requirements

- Fabric Loader 0.18.4+
- Fabric API
- Minecraft 1.21.11

## Installation

1. Install Fabric Loader and API for Minecraft 1.21.11
2. Download the mod JAR
3. Place the JAR in your `mods` folder
4. Launch the game

## Configuration

The config file is located at `config/tweakmoremore.json`. On first launch, a default config is created.

### Configuration Commands

- `/rule` - Lists all configured rules
- `/rule <name>` - Shows the value of a specific rule
- `/rule <name> <value>` - Sets a rule value (auto-detects type: boolean, int, float, or string)

### Config Keys

#### Block Properties
```
block.<block_name>.modifiers.collidable=bool
block.<block_name>.modifiers.resistance=float
block.<block_name>.modifiers.hardness=float
block.<block_name>.modifiers.toolRequired=bool
block.<block_name>.modifiers.randomTicks=bool
block.<block_name>.modifiers.slipperiness=float
block.<block_name>.modifiers.velocityMultiplier=float
block.<block_name>.modifiers.jumpVelocityMultiplier=float
block.<block_name>.modifiers.pistonBehavior=NORMAL/DESTROY/BLOCK/IGNORE/PUSH_ONLY
block.<block_name>.modifiers.dynamicBounds=bool
```

#### Item Properties
```
item.<item_name>.truncateItemCountShown=bool
item.<item_name>.maxCountPerStack=int
item.<item_name>.stackSize=int
item.<item_name>.useCooldown=float
item.<item_name>.damageMultiplier=float
item.<item_name>.damageMultiplerPlayersInclude=@selector
item.<item_name>.damageMultiplerPlayersExclude=@selector
```

#### Food Properties
```
food.<item_name>.nutrition=float
food.<item_name>.saturation=float
food.<item_name>.eattime=float
food.<item_name>.alwaysEdible=bool
```

#### Mob Caps
```
mob_cap.<mob_type>=int
mob_cap.chunk_area=int
```

#### XP & Drops
```
xpDroppingBlock.<block_name>.minExp=int
xpDroppingBlock.<block_name>.maxExp=int
experience_bottle.xpDropEquation=equation
player.xp.dropFormula= (equation, input: experienceLevel)
```

#### Enchantments
```
enchantment.codec.weight=int
enchantment.codec.max_level=int
```

#### Entity Settings
```
entity.piglin.admireGoldPriority=int
entity.piglin.findGoldSpeedModifier=float
entity.piglin.findGoldRadius=int
entity.piglin.findGoldTimeTicks=int
entity.piglin.refuseTradeCooldownTicks=int
entity.piglin.tradeTime=int
entity.piglin.admireGoldPriority=int
```

#### Villager Settings
```
villager.item_pickup_range=int
villager.item_sense_horizontal=int
villager.item_sense_vertical=int
```

#### Snow Golem
```
snow_golem.shoot_cooldown=int
snow_golem.shoot_range=float
snow_golem.hurt_by_water=bool
```

#### Brewing
```
brewing_stands.brewTime=int
brewing_stands.brewFuel=int
brewing_stands.tick=bool
```

#### Composter
```
composterCompostDelay=int
```

#### Hunger/Regen
```
regen.fast_interval=int
regen.slow_interval=int
regen.starvation_interval=int
```

#### Damage
```
damage.invulnerability_ticks=int
damage.hurt_animation_ticks=int
```

#### Mob Spawner
```
mob_spawner.min_spawn_delay=int
mob_spawner.max_spawn_delay=int
mob_spawner.spawn_count=int
mob_spawner.max_nearby_entities=int
mob_spawner.required_player_range=int
mob_spawner.spawn_range=int
```

#### World Ticks
```
tickWeather=bool
tickTime=bool
tick_raids=bool
sleep_never_skip=bool
tickBlockEntities=bool
tickWorldBorder=bool
```

#### Miscellaneous
```
allowPlaceSameFluidAndBlock=bool
attribute_swap_fix.enabled=bool
commands.forceRandomTick=bool
```

## Mod Commands

| Command | Description |
|---------|-------------|
| `/forcetick <pos>` | Force a random tick on the block at the specified position (requires `commands.forceRandomTick=true`) |

## Author

winapiadmin
