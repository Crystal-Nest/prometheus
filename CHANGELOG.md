# Change Log

All notable changes to the "prometheus" Minecraft mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Crystal Nest Semantic Versioning](https://crystalnest.it/#/versioning).

## [Unreleased]

- Nothing new.

## [v1.2.5] - 2025/12/21

- Fixed fire component blocks not having the correct fire type (revert of previous change).
- Fixed some code not being update to support multiple-value Fire Components.

## [v1.2.4] - 2025/12/21

- 1.21/1.21.1 only: fixed crash on NeoForge (`Fire#getValue(ResourceLocaiton)` now properly return `null` if there is no value).

## [v1.2.3] - 2025/12/21

- Fixed fire charges randomly not having projectile behavior on Fabric.
- Fixed fire charges reverting to normal fire when going through water.
- Fixed automatically adding fire charge components to the `creeper_igniters` tag.

## [v1.2.2] - 2025/12/20

- Automatically add fire charge components to the `creeper_igniters` tag.
- Fixed block fire components not being automatically registered to the cutout render layer.

## [v1.2.1] - 2025/12/20

- 1.21.6+ only: Fix crash when spawning fire charge entities.

## [v1.2.0] - 2025/12/20

- Ported to 1.21.11.
- Fixed `CustomCampfireBlockEntity`, preventing crashes when placing campfires.
- Fixed `CustomFireBlock` to "survive" only when on a sturdy block face.
- Fire Components can now support multiple values.
- Added lantern, torch, and wall torch fields in DDFs.
- DDFs fields `source`, `campfire`, `lantern`, `torch`, and `wallTorch` now accept either a string or a list of strings, as long as every string is a valid `ResourceLocation`.
- `LanternBlock` and `TorchBlock` now implement `FireTypeChanger`.
- Most implementations of `FireTypeChanger` now `ensure` their fire type is valid before returning it in `getFireType()`.
- Methods for fire-related game objects registration have now been moved to the new `FireRegistrar` class.
- Added new methods for fire-related game objects registration in `FireRegistrar` to support multiple-valued Fire Components.
- Added new methods for fire-related game objects registration in `FireRegistrar` to register multiple fire components in one call.
- Methods for fire-related game objects registration in `FireManager` have been deprecated, their logic is delegated to `FireRegistrar`, and have been marked for removal.
- New methods have been added to `Fire`, `Fire.Builder`, and `FireManager` to support multiple-valued Fire Components.
- Added new constructor to `CustomWallTorchBlock` to support multiple-valued Fire Components.
- JavaDoc has been updated for all the above changes, along with the [Wiki](https://github.com/Crystal-Nest/prometheus/wiki).
- Fixed `CustomLanternBlocks` not being automatically registered to `CUTOUT` render type on Fabric.
- Added new Fire Component: `Fire.Component#FIRE_CHARGE_ITEM` for fire charges.
- Added new method `FireManager#getFireType(Fire.Component, Object)` to retrieve the fire type from a Fire.Component value (useful for fire components that don't implement `FireTyped`, like items).

## [v1.1.2] - 2025/11/29

- Added Soul Fire type into `FireManager`.
- 1.21.10 only: added Copper Fire type into `FireManager`.

## [v1.1.1] - 2025/11/26

- Fixed [#6](https://github.com/Crystal-Nest/prometheus/issues/6), mod crashing on NeoForge when many mods are loaded.

## [v1.1.0] - 2025/11/11

- Ported to 1.21.10.
- Fixed [#2](https://github.com/Crystal-Nest/prometheus/issues/2), fire overlays not displaying correctly on Fabric.
- Minor improvements to the API.

## [v1.0.0] - 2025/11/10

- Ported to 1.21.10.

## [v1.0.0] - 2025/11/08

- Ported to 1.21.6/1.21.7/1.21.8.

## [v1.0.0] - 2025/11/06

- Ported Soul Fire'd API.
- Updated 1.21/1.21.1 to match 1.21.3+ interface.
- Fixed [#1](https://github.com/Crystal-Nest/prometheus/issues/1).

[Unreleased]: https://github.com/crystal-nest/prometheus
[README]: https://github.com/crystal-nest/prometheus#readme

[v1.2.5]: https://github.com/crystal-nest/prometheus/releases?q=1.2.5
[v1.2.4]: https://github.com/crystal-nest/prometheus/releases?q=1.2.4
[v1.2.3]: https://github.com/crystal-nest/prometheus/releases?q=1.2.3
[v1.2.2]: https://github.com/crystal-nest/prometheus/releases?q=1.2.2
[v1.2.1]: https://github.com/crystal-nest/prometheus/releases?q=1.2.1
[v1.2.0]: https://github.com/crystal-nest/prometheus/releases?q=1.2.0
[v1.1.2]: https://github.com/crystal-nest/prometheus/releases?q=1.1.2
[v1.1.1]: https://github.com/crystal-nest/prometheus/releases?q=1.1.1
[v1.1.0]: https://github.com/crystal-nest/prometheus/releases?q=1.1.0
[v1.0.0]: https://github.com/crystal-nest/prometheus/releases?q=1.0.0
