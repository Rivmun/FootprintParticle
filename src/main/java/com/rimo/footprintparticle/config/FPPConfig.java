package com.rimo.footprintparticle.config;

import com.rimo.footprintparticle.FPPClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.*;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.*;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;

import java.util.Arrays;
import java.util.List;

@Config(name = FPPClient.MOD_ID)
public class FPPConfig implements ConfigData {

	@EnumHandler(option = EnumDisplayOption.BUTTON)
	private WorkMode enableMod = WorkMode.ALL;

	@BoundedDiscrete(max = 30)
	private int wetDuration = 10;

	@BoundedDiscrete(min = 1, max = 10)
	private int secPerPrint = 5;

	private float printLifetime = 5.0f;

	private float watermarkLifetime = 5.0f;

	@BoundedDiscrete(max = 10)
	@Tooltip
	private int lifeTimeAcc = 0;

	@BoundedDiscrete(max = 10)
	private int footprintAlpha = 7;

	@BoundedDiscrete(max = 10)
	private int watermarkAlpha = 4;

	@BoundedDiscrete(min = -8, max = 8)
	@Tooltip
	private int printHeight = 0;

	@Tooltip
	private List<String> applyBlocks = DEF_APPLYBLOCKS;

	@Tooltip
	private float hardnessGate = 0.7f;

	@Tooltip
	private List<String> excludedBlocks = DEF_EXCLUDEDBLOCKS;

	@Tooltip
	private List<String> blockHeight = DEF_BLOCKHEIGHT;

	private boolean canGenWhenInvisible = true;

	@Tooltip
	private List<String> excludedMobs = DEF_MODS;

	@Tooltip
	private List<String> mobInterval = DEF_MOB_INTERVAL;

	@Tooltip
	private List<String> sizePerMob = DEF_SIZE;

	@Tooltip
	private List<String> horseLikeMobs = DEF_FOUR_LEGS;

	@Tooltip
	private List<String> spiderLikeMobs = DEF_EIGHT_LEGS;

	@Tooltip
	private List<String> customPrint = DEF_CUSTOM_PRINT;

	@Tooltip
	private int footprintSize = 5;

	public int isEnable() {return enableMod.ordinal();}
	public float getSecPerPrint() {return secPerPrint / 10f;}
	public float getPrintLifetime() {return printLifetime;}
	public float getWatermarkLifetime() {return watermarkLifetime;}
	public int getLifeTimeAcc() {return lifeTimeAcc;}
	public float getPrintHeight() {return printHeight / 0.0625f;}
	public List<String> getApplyBlocks() {return applyBlocks;}
	public List<String> getBlockHeight() {return blockHeight;}
	public List<String> getExcludedBlocks() {return excludedBlocks;}
	public boolean getCanGenWhenInvisible() {return canGenWhenInvisible;}
	public List<String> getExcludedMobs() {return excludedMobs;}
	public List<String> getSizePerMob() {return sizePerMob;}
	public List<String> getHorseLikeMobs() {return horseLikeMobs;}
	public List<String> getSpiderLikeMobs() {return spiderLikeMobs;}
	public int getWetDuration() {return wetDuration;}
	public float getWatermarkAlpha() {return watermarkAlpha / 10f;}
	public float getFootprintAlpha() {return footprintAlpha / 10f;}
	public List<String> getMobInterval() {return mobInterval;}
	public int getFootprintSize() {return footprintSize;}
	public List<String> getCustomPrint() {return customPrint;}
	public float getHardnessGate() {return hardnessGate;}


	@Category("misc")
	@BoundedDiscrete(max = 10)
	private int railFlameRange = 2;

	@Category("misc")
	private boolean enableBoatTrail = true;

	@Category("misc")
	@EnumHandler(option = EnumDisplayOption.BUTTON)
	private WorkMode swimPopLevel = WorkMode.ALL;

	@Category("misc")
	@EnumHandler(option = EnumDisplayOption.BUTTON)
	private WorkMode snowDustLevel = WorkMode.ALL;

	@Category("misc")
	@EnumHandler(option = EnumDisplayOption.BUTTON)
	private WorkMode waterSplashLevel = WorkMode.PLAYER_ONLY;

	public float getRailFlameRange() {return railFlameRange / 10f;}
	public boolean isEnableBoatTrail() {return enableBoatTrail;}
	public int getSwimPopLevel() {return swimPopLevel.ordinal();}
	public int getSnowDustLevel() {return snowDustLevel.ordinal();}
	public int getWaterSplashLevel() {return waterSplashLevel.ordinal();}


	public enum WorkMode {
		DISABLED("text.footprintparticle.disabled"),
		PLAYER_ONLY("text.footprintparticle.player_only"),
		ALL("text.footprintparticle.all");

		private final String key;

		WorkMode(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return this.key;
		}
	}

	@Excluded
	private static final List<String> DEF_APPLYBLOCKS = Arrays.asList(
			"#minecraft:wool"
	);
	@Excluded
	private static final List<String> DEF_BLOCKHEIGHT = Arrays.asList(
			"minecraft:snow,0.125",
			"minecraft:soul_sand,0.125",
			"minecraft:mud,0.125"
	);
	@Excluded
	private static final List<String> DEF_EXCLUDEDBLOCKS = Arrays.asList(
			"minecraft:beehive",
			"#minecraft:flower",
			"#minecraft:crop",
			"#minecraft:leaves",
			"#minecraft:sapling",
			"#minecraft:replaceable_plants"
	);
	@Excluded
	private static final List<String> DEF_MODS = Arrays.asList(
			"#aquatic",
			"minecraft:parrot",
			"minecraft:bee",
			"minecraft:allay",
			"minecraft:bat",
			"minecraft:phantom",
			"minecraft:endermite",
			"minecraft:blaze",
			"minecraft:ghast",
			"minecraft:wither",
			"minecraft:ender_dragon"
	);
	@Excluded
	private static final List<String> DEF_SIZE = Arrays.asList(
			"minecraft:chicken,0.6",
			"minecraft:pig,0.8",
			"minecraft:cat,0.5",
			"minecraft:ocelot,0.5",
			"minecraft:wolf,0.6",
			"minecraft:sniffer,1.6",
			"minecraft:enderman,0.6",
			"minecraft:slime,2",
			"minecraft:magma_cube,2",
			"minecraft:creeper,0.8",
			"minecraft:iron_golem,1.2",
			"minecraft:ravager,2",
			"minecraft:armadillo,0.7"
	);
	@Excluded
	private static final List<String> DEF_FOUR_LEGS = Arrays.asList(
			"minecraft:horse",
			"minecraft:donkey",
			"minecraft:mule",
			"minecraft:zombie_horse",
			"minecraft:skeleton_horse",
			"minecraft:camel",
			"minecraft:sniffer,0.8",
			"minecraft:ravager,0.5",
			"minecraft:creeper,0.3"
	);
	@Excluded
	private static final List<String> DEF_EIGHT_LEGS = Arrays.asList(
			"minecraft:spider",
			"minecraft:cave_spider",
			"minecraft:camel,0.3",
			"minecraft:sniffer,0.3",
			"minecraft:iron_golem,0.3",
			"minecraft:ravager,0.3"
	);
	@Excluded
	private static final List<String> DEF_MOB_INTERVAL = Arrays.asList(
			"minecraft:spider,0.5",
			"minecraft:cave_spider,0.5",
			"minecraft:camel,2",
			"minecraft:sniffer,3",
			"minecraft:iron_golem,2",
			"minecraft:creeper,0.8"
	);
	@Excluded
	private static final List<String> DEF_CUSTOM_PRINT = Arrays.asList(
			"mod_id:mob_id,fileName_NoExtend"
	);

}
