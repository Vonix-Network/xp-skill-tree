# XP Skill Tree

Minecraft 1.18.2 Forge mod by Vonix.Network. Press **K** to open a server-authoritative radial talent tree and spend Minecraft XP levels on connected skills.

## Features

- **41 skills** across eight colored paths: Arcane, Flame, Frost, Storm, Guardian, Ranger, Void, and Vitality
- Radial layout with a central Awakening node, branch connectors, milestone mastery nodes, and color-coded states
- Hover detail panel with branch, effect, cost, and prerequisite information
- Zoom with the mouse wheel and pan by dragging the tree
- Server-side prerequisite and XP validation
- Capability persistence across sessions, death, login, respawn, and dimension changes
- Optional Iron's Spells 'n Spellbooks spell-power and mana integration

## Mod Description

XP Skill Tree adds a deep, server-authoritative radial progression system to Minecraft 1.18.2. Press **K** to open the **TALENT TREE** and spend experience levels to unlock 41 connected skills across eight paths: Arcane, Flame, Frost, Storm, Guardian, Ranger, Void, and Vitality. Explore colored branches, inspect skill details, zoom and pan across the tree, and build a character that matches your playstyle. Progress is stored per player and synchronized safely between server and client. Optional Iron's Spells 'n Spellbooks integration enhances compatible spell-power and mana effects when that mod is installed.

## Required Dependencies

The following dependency must be installed on both the client and the dedicated server:

- **Minecraft Java Edition 1.18.2**
- **Minecraft Forge 40.2.0 or newer, but below 41.0**

### Optional Dependency

- **Iron's Spells 'n Spellbooks for Forge 1.18.2** (`irons_spellbooks`): optional. The core skill tree works without it. When installed, compatible skills can affect spell power, maximum mana, and mana recovery. Use a release compatible with Minecraft 1.18.2 and your Forge installation.

This is a Forge mod and is not compatible with Fabric or NeoForge builds.

## Build

Requires Java 17.

```bash
./gradlew clean build
```

The distributable JAR is written to `build/libs/`.

## Compatibility

- Minecraft 1.18.2
- Forge 40.2.0+
- Iron's Spells integration is optional at runtime.

## License

MIT. Minecraft and Forge are not owned by this project.
