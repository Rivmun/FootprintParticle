package com.rimo.footprintparticle.config;

import com.rimo.footprintparticle.FPPClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = FPPClient.MOD_ID)
public class FPPConfig implements ConfigData {

	static final List<String> DEF_APPLYBLOCKS = Arrays.asList(
			"#minecraft:wool"
	);
	static final List<String> DEF_BLOCKHEIGHT = Arrays.asList(
			"minecraft:snow,0.125",
			"minecraft:soul_sand,0.125",
			"minecraft:mud,0.125"
	);
	static final List<String> DEF_EXCLUDEDBLOCKS = Arrays.asList(
			"minecraft:beehive",
			"#minecraft:flower",
			"#minecraft:crop",
			"#minecraft:leaves",
			"#minecraft:sapling",
			"#minecraft:replaceable_plants"
	);
	static final List<String> DEF_MODS = Arrays.asList(
			"minecraft:parrot",
			"minecraft:bee",
			"minecraft:allay",
			"minecraft:bat",
			"minecraft:phantom",
			"minecraft:cod",
			"minecraft:salmon",
			"minecraft:tropical_fish",
			"minecraft:pufferfish",
			"minecraft:squid",
			"minecraft:glow_squid",
			"minecraft:axolotl",
			"minecraft:dolphin",
			"minecraft:silverfish",
			"minecraft:endermite",
			"minecraft:blaze",
			"minecraft:ghast",
			"minecraft:wither",
			"minecraft:ender_dragon"
	);
	static final List<String> DEF_SIZE = Arrays.asList(
			"minecraft:chicken,0.6",
			"minecraft:pig,0.8",
			"minecraft:cat,0.5",
			"minecraft:ocelot,0.5",
			"minecraft:wolf,0.6",
			"minecraft:sniffer,1.5",
			"minecraft:enderman,0.6",
			"minecraft:slime,2",
			"minecraft:magma_cube,2",
			"minecraft:creeper,0.8",
			"minecraft:iron_golem,1.2",
			"minecraft:ravager,2"
	);
	static final List<String> DEF_FOUR_LEGS = Arrays.asList(
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
	static final List<String> DEF_EIGHT_LEGS = Arrays.asList(
			"minecraft:spider",
			"minecraft:cave_spider",
			"minecraft:camel,0.3",
			"minecraft:sniffer,0.3",
			"minecraft:iron_golem,0.3",
			"minecraft:ravager,0.3"
	);
	static final List<String> DEF_MOB_INTERVAL = Arrays.asList(
			"minecraft:spider,0.5",
			"minecraft:cave_spider,0.5",
			"minecraft:camel,2",
			"minecraft:sniffer,3",
			"minecraft:iron_golem,2",
			"minecraft:creeper,0.8"
	);
	static final List<String> DEF_CUSTOM_PRINT = Arrays.asList(
			"mod_id:mob_id,fileName_NoExtend"
	);

	private int enableMod = 2;
	private float secPerPrint = 0.5f;
	private float printLifetime = 5.0f;
	private float watermarkLifetime = 5.0f;
	private float printHeight = 0f;
	private int wetDuration = 10;
	private float watermarkAlpha = 0.4f;
	private float footprintAlpha = 0.7f;
	private int footprintSize = 5;
	private boolean canGenWhenInvisible = true;
	private List<String> applyBlocks = DEF_APPLYBLOCKS;
	private List<String> blockHeight = DEF_BLOCKHEIGHT;
	private List<String> excludedBlocks = DEF_EXCLUDEDBLOCKS;
	private List<String> excludedMobs = DEF_MODS;
	private List<String> sizePerMob = DEF_SIZE;
	private List<String> spiderLikeMobs = DEF_EIGHT_LEGS;
	private List<String> horseLikeMobs = DEF_FOUR_LEGS;
	private List<String> mobInterval = DEF_MOB_INTERVAL;
	private float railFlameRange = 0.2f;
	private boolean enableBoatTrail = true;
	private int swimPopLevel = 2;
	private int snowDustLevel = 2;
	private int waterSplashLevel = 1;
	private List<String> customPrint = DEF_CUSTOM_PRINT;

	public int isEnable() {return enableMod;}
	public float getSecPerPrint() {return secPerPrint;}
	public float getPrintLifetime() {return printLifetime;}
	public float getWatermarkLifetime() {return watermarkLifetime;}
	public float getPrintHeight() {return printHeight;}
	public List<String> getApplyBlocks() {return applyBlocks;}
	public List<String> getBlockHeight() {return blockHeight;}
	public List<String> getExcludedBlocks() {return excludedBlocks;}
	public boolean getCanGenWhenInvisible() {return canGenWhenInvisible;}
	public List<String> getExcludedMobs() {return excludedMobs;}
	public List<String> getSizePerMob() {return sizePerMob;}
	public List<String> getHorseLikeMobs() {return horseLikeMobs;}
	public List<String> getSpiderLikeMobs() {return spiderLikeMobs;}
	public int getWetDuration() {return wetDuration;}
	public float getWatermarkAlpha() {return watermarkAlpha;}
	public float getFootprintAlpha() {return footprintAlpha;}
	public float getRailFlameRange() {return railFlameRange;}
	public boolean isEnableBoatTrail() {return enableBoatTrail;}
	public int getSwimPopLevel() {return swimPopLevel;}
	public int getSnowDustLevel() {return snowDustLevel;}
	public int getWaterSplashLevel() {return waterSplashLevel;}
	public List<String> getMobInterval() {return mobInterval;}
	public int getFootprintSize() {return footprintSize;}
	public List<String> getCustomPrint() {return customPrint;}

	public void setEnableMod(int isEnable) {enableMod = isEnable;}
	public void setSecPerPrint(float sec) {secPerPrint = sec;}
	public void setPrintLifetime(float time) {printLifetime = time;}
	public void setWatermarkLifetime(float watermarkLifetime) {this.watermarkLifetime = watermarkLifetime;}
	public void setPrintHeight(float height) {printHeight = height;}
	public void setApplyBlocks(List<String> list) {applyBlocks = new ArrayList<>(list);}
	public void setBlockHeight(List<String> list) {blockHeight = new ArrayList<>(list);}
	public void setExcludedBlocks(List<String> list) {excludedBlocks = new ArrayList<>(list);}
	public void setCanGenWhenInvisible(boolean isEnable) {canGenWhenInvisible = isEnable;}
	public void setExcludedMobs(List<String> list) {excludedMobs = new ArrayList<>(list);}
	public void setSizePerMob(List<String> list) {sizePerMob = new ArrayList<>(list);}
	public void setHorseLikeMobs(List<String> list) {horseLikeMobs = new ArrayList<>(list);}
	public void setSpiderLikeMobs(List<String> list) {spiderLikeMobs = new ArrayList<>(list);}
	public void setWetDuration(int time) {wetDuration = time;}
	public void setWatermarkAlpha(float watermarkAlpha) {this.watermarkAlpha = watermarkAlpha;}
	public void setFootprintAlpha(float footprintAlpha) {this.footprintAlpha = footprintAlpha;}
	public void setRailFlameRange(float railFlameRange) {this.railFlameRange = railFlameRange;}
	public void setEnableBoatTrail(boolean enableBoatTrail) {this.enableBoatTrail = enableBoatTrail;}
	public void setSwimPopLevel(int swimPopLevel) {this.swimPopLevel = swimPopLevel;}
	public void setSnowDustLevel(int snowDustLevel) {this.snowDustLevel = snowDustLevel;}
	public void setWaterSplashLevel(int waterSplashLevel) {this.waterSplashLevel = waterSplashLevel;}
	public void setMobInterval(List<String> mobInterval) {this.mobInterval = mobInterval;}
	public void setFootprintSize(int footprintSize) {this.footprintSize = footprintSize;}
	public void setCustomPrint(List<String> customPrint) {this.customPrint = customPrint;}
}
