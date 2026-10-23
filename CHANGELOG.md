# 0.7.1
### Fixes & Improvements
- Fix particle texture will be missing until game restart, when change custom footprint particle resourcepack and reload in game.
- Fix saving config will crash the game that causing by configScreen push invalid value to IO system.
  - Add some validator to configScreen. Now 'Save & Quit' button will be disabled if there is invalid value until you correct it.
- Fix boat will spit out a lot of particle when placed.
- Refactor list lookup method for better performance, readability and others.
  - Now mob list can input tags starts with '#' symbol.
- Add an option to limited max generate distance of our particles. Default is 64 blocks.
- Changed particle direction always rely on entity facing instead of their movement direction if it has.
- Fix '#c' tag in default list cannot be matched on older MC version by replacing them to specific block name. (i.e. composter, froglight)
- Remove accesswidener file that no longer in use from Forge build.
- Improved performance of self-hosted particle system.
#### Large internal improvements that clean up a lot of dumb code I wrote before 0.6.0...
- Fix watermark will generate in air.
- Fix watermark will randomize its direction.
- Fix mixin tick will run meaningless on integrated-server.
- Refactor boat & minecart mixin, improved their performance.
- Improved code readability and performance of generator.
- Improved customPrint pick up performance.

# 0.7.0
#### Refactor footprint generator to fix some reference issue. (Thanks to @Redls07)
- Fix footprint generate on below block that player never touched.
  - Still have some small gap that was necessary to detect movement for remote player.
  - Add an option to config that gap (0.1-1.0 blocks).
- Fix footprint generate above player foot position when walk in non-full block.
- Fix footprint generate under pressure plate (now it disabled to gen in default).
- Fix footprint generate on some non-full block that shouldn't appear (i.e. candles).
- Now footprint can generate besides some non-full block at correct position (i.e. fence gate).
#### Other fixes and improvements since 0.6.0:
- Fix client without FPP cannot connect to LAN host with FPP on Fabric / Neoforge.
  - Forge not be affected because it already self-hold particle since 0.6.0.
- Fix tag item in configList cannot be hit and included / excluded.
- Fix PrintHeightOffset incorrectly applied to logical, it must be visual only.
- Fix boat & minecart misc function no functioning when their ridden by remote player.
- Improve whileList / excludedList priority logically.
- Add some default item to excludedList which blocks should be excluded.
- Add default config for new mob up to 26.3.
  - You must 'Reset' in configScreen to apply it, **remember backup your config**.
- Add more category to configScreen.

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
