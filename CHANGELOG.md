# 0.6.0
Move to stonecutter + arch-loom build system for multi-version/platform support.
- Add support for Neoforge 26.x, 1.21.11, 1.21.1.
  - MC 26.x version now is a merged jar both for Neoforge and Fabric via Forgix.
- Fix & improved footprint generation conditions.
  - Fix remote player cannot generation footprint.
  - Fix unbreakable block can be passed from hardness filter when gate set bigger than 1.
- Improved list lookup performance.
