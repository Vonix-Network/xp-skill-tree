# XP Skill Tree

Minecraft 1.18.2 Forge mod by Vonix.Network.

XP Skill Tree adds a server-authoritative branching skill tree. Players press **K** to open the client screen and spend Minecraft XP levels on connected nodes.

## Features

- Branching Fire and Mana paths
- Server-side prerequisite and XP validation
- Player capability persistence across sessions and death
- Client synchronization on login, respawn, and dimension changes
- Optional Iron's Spells 'n Spellbooks integration (`irons_spellbooks`)
- Spell power and max-mana attribute modifiers when Iron's Spells is installed
- Optional mana regeneration hook

## Build

Requires Java 17.

```bash
./gradlew clean build
```

The distributable JAR is written to `build/libs/`.

## Compatibility

- Minecraft 1.18.2
- Forge 40.2.0+
- Iron's Spells integration is optional at runtime and was implemented against the public 1.18.2 source branch.

## License

MIT. Minecraft and Forge are not owned by this project.
