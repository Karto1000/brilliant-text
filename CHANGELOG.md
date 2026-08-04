# Changelog

All notable changes to this project since the last release (v0.3.0 — 2026-08-01).

Unreleased (since v0.3.0 → main)

## Added
- New JSON-based configuration: `config/brilliant_text_bindings.json` and a `JsonConfigManager` to load shader bindings at runtime (replaces the old string-based Forge config bindings).
- Many new config helpers and adapters: `ShaderDefinition`, `NumberRange`, `HexColorAdapter`, `HexListColorAdapter`.
- New built-in shader/presentation features:
  - Particle configuration for shaders (texture, color, rarity, lifetime, dimensions, rotations, shrink behavior).
  - Wiper effect across text (configurable color and slowdown).
  - Support for lists of colors for text, outline and glow (smooth cycling between colors).
- New formatting code `§h` (diamond/blue look) + demo GIF `images/diamond_formatting.gif`.
- Default configuration and presets are created automatically when the JSON config is missing.
- A resource-pack-style class `VanillaItemRenameResourcePack` to replace vanilla item translations with shader-prefixed names (configurable via new config).
- New client lifecycle hook `postInit()` used to initialize JSON config loading.

## Changed
- Version bumped to 1.0.0:
  - `gradle.properties` mod_version updated to `1.0.0`.
  - `BrilliantText.VERSION` updated to `1.0.0`.
- README updated with detailed docs about the JSON shader binding format, shader definition fields, example JSON snippets and links to `ShaderDefinition.java` for reference.
- Shader and rendering APIs/uniforms changed:
  - Uniform `u_textureSize` renamed to `u_scaledScreenSize`.
  - `u_time` now passed as integer milliseconds rather than float seconds in code (shaders updated to match).
  - Outline shader now supports an optional wiper color + slowdown uniforms and uses scaled screen size.
- `IOutlinedTextShader` API expanded:
  - Methods now return NonNullList<Integer> for text/outline/glow colors (instead of Optional<Integer> or single ints).
  - Color transition durations are supported (smooth interpolation between colors).
  - New methods for wiper color and slowdown.
- `ITextShader` renderPass parameter and uniform handling tightened with @Nonnull annotations and updated uniform names.
- Particle and particle builder behavior:
  - Added support for particles that can shrink over lifetime (`shouldShrink`) and fixes to particle creation and rendering.
- `BrilliantTextRenderer` particle rendering: particles now optionally shrink while keeping their center fixed and fade with lifetime.
- `BrilliantParticle` and `BrilliantParticleBuilder` were moved / refactored (package changes and new fields like shouldShrink).
- `ColorHelper` refactored and expanded: new ARGB helper class, mix/darken/brighten utilities and smoothInterpolate to support color cycling.
- Language file `en_us.lang` updated: added `item.brilliant_text.diamond_item.name` and adjusted translation entries.
- Several classes moved/renamed and package reorganizations done (e.g., shader classes relocated, config classes added under `config`).

## Removed
- The old `handlers.ClientModRegistry` and the old Forge string-based `handlers.ForgeConfigHandler` were removed/cleaned up in favor of the new JSON config and simplified registries.
- Legacy config string format has been replaced by the JSON `brilliant_text_bindings.json` format.

## Internal / Refactor
- Many new utility classes and refactors to support the richer configuration and rendering features (JSON adapters, config manager, shader definition DSL).
- Mixins / resources list reorganized (`mixins.brilliant_text.vanilla.json` updated structure).
- Improved error handling and logging around config creation/loading.

## Notable file changes (high level)
- README.md — expanded documentation and examples for the new JSON config and shader definitions.
- gradle.properties — mod_version bumped to 1.0.0.
- src/main/java/brilliant_text/config/ — new config classes: ForgeConfigHandler, JsonConfigManager, ShaderDefinition, Hex adapters, NumberRange.
- src/main/java/brilliant_text/shader/ and shader/builtin — updates and refactors to particle & shader classes, new interfaces behavior.
- src/main/java/brilliant_text/proxy/ — ClientProxy updated to call new initialization steps (VanillaItemRenameResourcePack init & JsonConfigManager postInit).
- src/main/resources/assets/.../shaders/post/*.frag — `flame.frag` and `outline.frag` updated to use new uniforms and implement wiper/particle timing changes.
- images/diamond_formatting.gif — new demo image added.

## Upgrade notes / Breaking changes
- If you implemented custom shaders:
  - Update your shader implementations to use the new IOutlinedTextShader methods (getTextColors/getOutlineColors/getGlowColors) which return lists of colors rather than single ints or Optionals.
  - Respect the new uniform names in custom GLSL shaders: `u_scaledScreenSize` (vec2) and `u_time` (int milliseconds) instead of `u_textureSize` and float time.
  - If your shader uses a wiper effect or color cycling, support the new wiper uniforms and color transition durations.
- Configuration format changed: the previous Forge string-based bindings are no longer used. Convert bindings to `config/brilliant_text_bindings.json`. See README for examples and `ShaderDefinition.java` for fields.
- Resource / language injection may now be performed via `VanillaItemRenameResourcePack` depending on the config; review `ForgeConfigHandler.client` options.

## Links
- Compare changes: https://github.com/Karto1000/brilliant-text/compare/v0.3.0...main
- Latest release (previous): https://github.com/Karto1000/brilliant-text/releases/tag/v0.3.0

---
Generated automatically from commits on `main` since `v0.3.0`. Please review and adjust wording/sections as desired before publishing.
