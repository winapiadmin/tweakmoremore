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
- **Mace Smash**: Configurable fall-distance thresholds, per-tier damage rates, knockback range/power, and gliding requirement
- **Max Health Limit**: Raise the vanilla 1024 max-health cap
- **Explosions**: Per-explosive power, fire, block/entity damage modes
- **Bug Fixes**: Dozens of optional fixes for known vanilla bugs

## Requirements

- Fabric Loader 0.18.4+
- Fabric API
- Minecraft 1.21.11

## Configuration

Config file: `config/tweakmoremore.json` (or per-world `<world>/tweakmoremore.json`).

### Commands

| Command | Description |
|---------|-------------|
| `/rule` | List all configured rules |
| `/rule <name>` | Show a rule's value |
| `/rule <name> <value>` | Set a rule value |
| `/forcetick <pos>` | Force a random tick on a block (requires `commands.forceRandomTick=true`) |

## Documentation

- **[Configuration Reference](docs/configuration.md)** — full list of config keys, types, and defaults
- **[API Documentation](docs/api.md)** — architecture, core classes, and how to extend the mod

## Author

winapiadmin
