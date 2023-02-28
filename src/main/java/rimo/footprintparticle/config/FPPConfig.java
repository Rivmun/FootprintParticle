package rimo.footprintparticle.config;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import rimo.footprintparticle.FPPClient;

@Config(name = FPPClient.MODID)
public class FPPConfig implements ConfigData {

	static final List<String> DEF_APPLYBLOCKS = new ArrayList<>(List.of(
			//
	));
	static final List<String> DEF_EXCLUDEDBLOCKS = new ArrayList<>(List.of(
			//
	));
	static final List<String> DEF_MODS = new ArrayList<>(List.of(
			"minecraft:parrot",
			"minecraft:bee",
			"minecraft:allay",
			"minecraft:bat",
			"minecraft:phantom",
			"minecraft:squid",
			"minecraft:glow_squid",
			"minecraft:axolotl",
			"minecraft:dolphin",
			"minecraft:wither",
			"minecraft:ender_dragon"
	));
	static final List<String> DEF_SIZE = new ArrayList<>(List.of(
			"minecraft:chicken,0.3",
			"minecraft:enderman,0.35",
			"minecraft:pig,0.4"
	));

	private boolean enableMod = true;
	private float secPerPrint = 0.5f;
	private float printLifetime = 10.0f;
	private List<String> applyBlocks = DEF_APPLYBLOCKS;
	private List<String> excludedBlocks = DEF_EXCLUDEDBLOCKS;
	private List<String> excludedMobs = DEF_MODS;
	private List<String> sizePerMob = DEF_SIZE;

	public boolean isEnable() {return enableMod;}
	public float getSecPerPrint() {return secPerPrint;}
	public float getPrintLifetime() {return printLifetime;}
	public List<String> getApplyBlocks() {return applyBlocks;}
	public List<String> getExcludedBlocks() {return excludedBlocks;}
	public List<String> getExcludedMobs() {return excludedMobs;}
	public List<String> getSizePerMob() {return sizePerMob;}

	public void setEnableMod(boolean isEnable) {enableMod = isEnable;}
	public void setSecPerPrint(float sec) {secPerPrint = sec;}
	public void setPrintLifetime(float time) {printLifetime = time;}
	public void setApplyBlocks(List<String> list) {applyBlocks = new ArrayList<>(list);}
	public void setExcludedBlocks(List<String> list) {excludedBlocks = new ArrayList<>(list);}
	public void setExcludedMobs(List<String> list) {excludedMobs = new ArrayList<>(list);}
	public void setSizePerMob(List<String> list) {sizePerMob = new ArrayList<>(list);}

}
