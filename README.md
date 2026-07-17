# Ancient Structures: Edo Japan

[v0.2.0] — a Minecraft 1.21.1 mod (Fabric + NeoForge) for the *Ancient Structures* series, adding Japanese Edo-period themed structures, statues, and dungeons to world generation.

## Requirements

- Minecraft 1.21.1
- [Dawn of Time Builder](https://www.curseforge.com/minecraft/mc-mods/dawn-of-time-builder) >= 1.5.13
- [Armor of the Ages](https://www.curseforge.com/minecraft/mc-mods/armor-of-the-ages) >= 1.3.5

This mod is data-driven (structures, loot tables, worldgen) with no custom blocks, items, or entities of its own — it builds on blocksets from Dawn of Time and armor sets from Armor of the Ages.

## What it adds

### Structures
Generated via jigsaw worldgen, grouped into structure sets:

- **Player structures** (`edo_player` biomes: windswept forest, birch forest, taiga, flower forest) — Samurai House, Mini Castle
- **Shrines** (`edo_shrine` biomes: cherry grove, grove, windswept forest/gravelly hills, flower forest) — 5 small shrines, 2 torii shrines
- **Temples** (`edo_temple` biomes: taiga, birch forest, flower forest) — Medium shrine, Large shrine, Grand Buddha, Buddhist Temple
- **Dungeons** (`edo_dungeon` biomes: taiga, birch forest, old growth birch/spruce/pine taiga) — Mini Cemetery, Kofun
- **Illager outposts** (`edo_illager` biomes: same as dungeons) — Mini Fort, Wako Outpost
- **Statues** (`edo_statue` biomes: taiga, birch forest, old growth birch/spruce/pine taiga) — Buddha, Frog, Illager, Kitsune, Tanuki, Villager

### Loot
Custom loot tables for exterior, farming, interior, and shrine chests, including Japanese-themed and rare Japanese loot pools, with some loot deliberately hidden for exploration.

### Mobs
No custom mobs are spawned naturally. Instead, the mod ships `/function` commands that summon themed variants using Armor of the Ages equipment, meant to be given out via command/spawn eggs:

- `edo_japan:mobs/o_yoroi_skeleton` — Skeleton in Ō-yoroi armor with an enchanted bow
- `edo_japan:mobs/o_yoroi_skeleton_rider`
- `edo_japan:mobs/do_maru_skeleton`
- `edo_japan:mobs/wither_skeleton_o_yoroi`
- `edo_japan:mobs/wither_skeleton_raijin` — Wither Skeleton in Raijin armor

## Building

Multi-loader Gradle project (`common` / `fabric` / `forge`).

```
./gradlew build
```

## License

GNU GPL 3.0
