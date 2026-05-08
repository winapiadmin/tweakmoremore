# tweakmoremore

This mod tweaks to (almost) every extent of Minecraft

Note that the settings are not pre-populated, except for items and blocks.

Also note that the config might change in an unpredictable way so the mod will crash the game if the config didn't support the supposed version.

## What's in the mod

- Item stacks' size modification (partial credit: https://modrinth.com/mod/stackable127, will soon be modified)
- Vanilla's fluids (not implemented for mods)
- Block metadata (not working), experience drops
- Food properties
- Item cooldown, stack size
- A little bit of ticking behavior
- Inventory slots index
- Player falling distance (works in client-side too!)
- Custom player XP drop when dead instead of 100 XP points
- Damage info to game log
- Enchantment max level (networking only)
- Explosion data: creating fire, power, damage players (or specific players), break blocks, enhanced logics if enabled
- Composter compost delay
- Brewing Stand brewing time
- Prevent players from double-placing water when there's still water
- Experience bottles XP drop
- Per-world (for some of the configs) config

## Reporting issues

See the [issue tracker](https://github.com/winapiadmin/tweakmoremore/issues)

## Installation instructions
Every release has 2 ZIP files: one for JDK 25 (not backward compatible) and one for JDK 21 (always compatible)

and each ZIP file has:
1. devlibs folder for development JAR files
2. libs (which contain the mod)

**Pick: libs/tweakmoremore-\<version\>.jar**

1.
>[!CAUTION]
>Do **NOT** pick any development library (devlibs) or libs/tweakmoremore-\<version\>-sources.jar.
>
>TLDR: 
>- **Pick: libs/tweakmoremore-\<version\>.jar**
>- **NOT PICK**: 
>- - libs/tweakmoremore-\<version\>-sources.jar
>- - devlibs/tweakmoremore-\<version\>-dev.jar
>- - devlibs/tweakmoremore-\<version\>-sources.jar**
2. 
>[!TIP]
>Check your JRE/JDK version.
>
>- MultiMC or whatever launcher other than the official one: check out the settings and run `java --version`.
>- Minecraft Launcher (offical): JRE 21.

>[!CAUTION]
>Do **NOT** use build-artifacts-25-temurin.zip's JARs when your JRE/JDK version is 21 or older than 25.

I am **NOT** responsible for using unsupported versions of JRE/JDK and JAR files.
