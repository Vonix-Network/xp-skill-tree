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
