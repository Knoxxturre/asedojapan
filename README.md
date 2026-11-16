# Ancient Structures: Edo Japan

[v0.1.4.1] — a Minecraft 1.20.1 mod (Fabric + Forge) for the *Ancient Structures* series, adding Japanese Edo-period themed structures, statues, and dungeons to world generation.

## Requirements

- Minecraft 1.20.1
- [Dawn of Time Builder](https://www.curseforge.com/minecraft/mc-mods/dawn-of-time-builder) >= 1.5.54
- [Armor of the Ages](https://www.curseforge.com/minecraft/mc-mods/armor-of-the-ages) >= 1.3.16

Structures, loot tables and worldgen are data-driven, built on blocksets from Dawn of Time. The mod's own mobs wear armor sets from Armor of the Ages.

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
No mobs spawn naturally. Each one has a spawn egg in the Spawn Eggs creative tab and always wears its Armor of the Ages kit.

Guards (villagers that defend the area around where they were placed):

- Villager Samurai — Japanese light armor, sword and shield
- Villager Oyumi — Japanese light armor, crossbow
- Villager O-Yoroi Samurai — O-Yoroi armor, sword and shield
- Villager O-Yoroi Oyumi — O-Yoroi armor, crossbow

Hostile:

- Wither Raijin Samurai — Wither Skeleton in Raijin armor with a sword
- Wither Raijin Oyumi — Wither Skeleton in Raijin armor with a Power V bow
- Skeleton O-Yoroi Samurai — Skeleton in O-Yoroi armor with a sword
- Skeleton O-Yoroi Oyumi — Skeleton in O-Yoroi armor with a Power V bow
- Skeleton O-Yoroi Samurai Rider — Skeleton O-Yoroi Samurai on a skeleton horse
- Skeleton O-Yoroi Oyumi Rider — Skeleton O-Yoroi Oyumi on a skeleton horse
- Zombie Samurai — Zombie in Japanese light armor with a sword
- Skeleton Samurai — Skeleton in Japanese light armor with a sword
- Skeleton Oyumi — Skeleton in Japanese light armor with a Power V bow
- Pillager Oyumi — Pillager in Japanese light armor with a crossbow
- Vindicator Samurai — Vindicator in Japanese light armor with a sword

## Building

Multi-loader Gradle project (`common` / `fabric` / `forge`).

```
./gradlew build
```

## License

GNU GPL 3.0
