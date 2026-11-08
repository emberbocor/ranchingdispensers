# Ranching Dispensers

Feed, milk and collect mooshroom stew with redstone and vanilla dispensers.

Ranching Dispensers turns ordinary dispensers into ranching tools for cows, mooshrooms, sheep, pigs, chickens, rabbits and goats. Supply the right food, empty buckets or bowls, then send a redstone pulse.

Available for NeoForge, Forge and Fabric.

## Features

- **Feed adults.** Ready animals enter vanilla love mode, with normal cooldowns and hearts. A partner need not already be present to feed one animal.
- **Grow babies.** Food uses vanilla growth, whole-second rounding and forced-age behavior.
- **Milk animals.** Empty buckets milk adult cows, mooshrooms and goats, after vanilla fluid pickup gets its chance.
- **Collect stew.** Bowls collect mushroom stew from adult mooshrooms, or suspicious stew with all prepared effects from a brown mooshroom.
- **Optional wool regrowth.** With Sheep Wool Regrowth installed, wheat restores a sheared adult sheep's wool before breeding, keeping its color.
- **Keep unused supplies.** Supported food and containers stay inside when their enabled action has no eligible target.

## How it works

Point a **vanilla dispenser** at the block containing your animals and give it a redstone pulse. All six facing directions work. Only animals whose bounding boxes overlap the single block immediately in front can be selected. The nearest eligible animal is chosen by distance from the dispenser's center; entity ID breaks equal-distance ties. An unsuitable or unready animal does not block another eligible animal in that space.

The dispenser still selects one nonempty slot at random. Each pulse performs at most one ranching action, consumes one item and affects one animal. There is no smarter slot selection, larger search area or feeding between pulses. Droppers keep normal transfer and ejection behavior.

- Food follows Minecraft's animal food tags and `isFood` checks, including data pack changes after reload. Defaults are wheat for cows/mooshrooms/sheep/goats; carrots, potatoes or beetroot for pigs; chicken seeds; and carrots, golden carrots or dandelions for rabbits. Only the seven listed entity types and their vanilla variants are included.
- Adults need age zero and must not already be in love. Babies gain about 10% of remaining growth time, rounded down to whole seconds. Near adulthood, a valid meal can consume food while adding zero seconds of growth; no minimum growth is imposed.
- Existing special dispenser handlers take priority over feeding, even if a data pack marks their items as food. Bone meal, projectiles, armor, shears and filled buckets retain their handlers. Flowers that prepare a brown mooshroom's effects must still be given by a player.
- An empty bucket first attempts original fluid pickup once. If that fails, a valid adult can supply milk, without an added cooldown. Successful fluid pickup ends that pulse.
- One empty container becomes one result. If the input stack runs out, its slot holds the result. Otherwise the result is inserted into the dispenser; if there is no space, it is ejected. Hoppers still move items normally.
- Suspicious stew consumes all prepared effects. The next bowl yields ordinary mushroom stew until a player prepares another effect.
- Optional wool regrowth works even while an adult is on breeding cooldown or already in love. That pulse only regrows wool; a later pulse can breed when ready. Without Sheep Wool Regrowth, wheat follows ordinary breeding rules.
- Feeding has no player owner and gives no dispenser-related player credit. Breeding XP stays vanilla; a mate genuinely fed by a player can still provide that player's normal breeding credit.
- Feedback uses vanilla particles, sounds and entity events. Baby feeding sends a happy-villager particle from the server when accepted; it does not reproduce the client-predicted 40-tick particle sequence of a player's click. Ranching dispenser feedback plays once per pulse, including result overflow.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 1.21.1 | 1.21.1 | 1.21.1 |
| Java | 21 | 21 | 21 |
| Loader | NeoForge 21.1.1 or newer | Forge 52.0.0 or newer | Fabric Loader 0.15.11 or newer |
| Other | | | Fabric API 0.102.0+1.21.1 or newer for Minecraft 1.21.1 |
| Jar | `ranchingdispensers-neoforge-1.21.1-<version>.jar` | `ranchingdispensers-forge-1.21.1-<version>.jar` | `ranchingdispensers-fabric-1.21.1-<version>.jar` |

On Minecraft 1.21.1, NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your game's loader.

### Client and server

Install the matching jar on the **server**. Clients can join without Ranching Dispensers, using vanilla particles, sounds and inventory updates. Fabric API is required on a Fabric server. No client installation or custom networking is needed for these actions.

In singleplayer, install it in your own game; the built-in server runs it. Fabric singleplayer also needs Fabric API.

## Configuration

`config/ranchingdispensers-server.toml` is created with comments on the first server or world start. Exactly five options, all default `true`:

```toml
enabled = true
feedAdults = true
feedBabies = true
milkAnimals = true
collectStew = true
```

Restart the server or reopen the singleplayer world after editing. Every startup reads a fresh settings snapshot. There is no config screen or hot reload.

`enabled = false` restores all original dispenser behavior. Each other switch releases its action to the original behavior. `feedAdults` also controls optional wool regrowth; `feedBabies` is independent. While either feeding action is enabled, supported food remains stored when no animal satisfies an enabled feeding action. With milking or stew collection enabled, unused empty buckets or bowls remain stored.

Missing keys use defaults. Invalid booleans use only the affected key's default with a warning. Malformed or unreadable files use defaults and are left unchanged. Existing files are never rewritten to add keys. Config is server-local and is not synchronized to clients.

## Saved data and removing the mod

Animal age and forced age, love timer, sheep wool/color, prepared mooshroom stew effects and dispenser contents use existing vanilla save data. The temporary growth-particle timer is not synchronized or saved. No custom world file, entity marker or timer is added; config is separate from world data.

Removing the mod restores vanilla dispensing. Growth, breeding, wool changes and produced items remain ordinary vanilla state and follow normal saving and aging rules. There are no modded blocks or items to convert.

## Compatibility

- Targets Minecraft 1.21.1. Use one matching loader jar at a time.
- Sheep Wool Regrowth is optional and detected by mod ID, with no required dependency or direct class linkage.
- Growth and breeding keep vanilla methods so cooldown hooks such as Instant Breeding can apply. Other mods can still change eligibility, growth, breeding and feedback.
- Data packs can change food tags. Items with special dispenser handlers retain those handlers; adding them to a food tag does not enable automatic feeding with them.
- Mods replacing dispenser behaviors or injecting into the same default/fallback execution may take priority or conflict. Smart Dispensers, Carpet Extra and arbitrary dispenser modifications have no compatibility guarantee.

## Building from source

Use JDK 21 and the included Gradle wrapper:

```
./gradlew build
```

Release jars are written to `neoforge/build/libs/`, `forge/build/libs/` and `fabric/build/libs/`. Use Fabric's remapped jar from `fabric/build/libs/`; development jars are not release artifacts.

Development profiles use `runs/1.21.1/neoforge` for both NeoForge sides, and separate `runs/1.21.1/forge-client`, `forge-server`, `fabric-client` and `fabric-server` directories. These directories are ignored by Git.

## License

MIT, copyright emberbocor. See [LICENSE](LICENSE).

The original NeoForged template notice is retained in [TEMPLATE_LICENSE.txt](TEMPLATE_LICENSE.txt).

The Fabric jar bundles unmodified NightConfig core and toml 3.8.0 under LGPL-3.0, with notice, source links and LGPL/GPL texts in `META-INF/`. NeoForge and Forge use their loader-provided NightConfig libraries.
