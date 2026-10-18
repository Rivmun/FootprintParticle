# 0.6.2
Refactor footprint generator to fix some reference issue. (Thanks to @Redls07)
- Fix footprint generate on below block that player never touched.
  - (Still have some small gap that was necessary to detect movement of remote player.)
- Fix footprint generate above player foot position when walk in non-full block.
- Fix footprint generate under pressure plate.
- Fix footprint generate on some non-full block that shouldn't appear (i.e. candle).
- Now footprint can generate besides some non-full block at correct position (i.e. fence gate).
Other fixes and improvements since 0.6.0:
- Fix tag item in configList cannot be hit and included / excluded.
- Fix PrintHeightOffset incorrectly applied to logical, it must be visual only.
- Fix boat & minecart misc function no functioning when their ridden by remote player.
- Improve whileList / excludedList priority logically.
- Add some default item to excludedList which blocks should be excluded.
- Add default config for new mob up to 26.3.

# 0.6.1
- Hotfix boot refusal on 1.16.5-fabric due to ModId of 'fabric-api' is 'fabric' in that version.

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
