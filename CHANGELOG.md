# 0.6.0
### Breaking Update
NOTICE: Although we have ensured compatibility, we **still recommend backing up your config file** before upgrading.
- Move to stonecutter + arch-loom build system for multi-version/platform support.
- Add support for Neoforge 26.x, 1.21.11, 1.21.1.
  - MC 26.x version now is a merged jar both for Neoforge and Fabric via Forgix.
- Fix Forge side cannot play on Client-only.
- Fix & improved footprint generation conditions.
  - Fix remote player cannot generation footprint.
  - Fix unbreakable block can be passed from hardness filter when gate set bigger than 1.
- Config System Refactored.
  - Improved list lookup performance via using Map instead of List in config.
  - Cloth Config now optional to build ConfigScreen.
  - Restored the Cloth Config Screen instead of AutoConfig Screen.
