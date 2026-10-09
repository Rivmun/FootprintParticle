package com.rimo.footprintparticle.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config {
	private WorkMode enableMod = DEF_ENABLE_MOD;
	private int wetDuration = DEF_WET_DURATION;
	private int secPerPrint = DEF_SEC_PER_PRINT;
	private float printLifetime = DEF_PRINT_LIFETIME;
	private float watermarkLifetime = DEF_WATERMARK_LIFETIME;
	private int lifeTimeAcc = DEF_LIFETIME_ACC;
	private int footprintAlpha = DEF_FOOTPRINT_ALPHA;
	private int watermarkAlpha = DEF_WATERMARK_ALPHA;
	private int printHeight = DEF_PRINT_HEIGHT;
	private Set<String> applyBlocks = toSet(DEF_APPLYBLOCKS);
	private float hardnessGate = DEF_HARDNESS_GATE;
	private Set<String> excludedBlocks = toSet(DEF_EXCLUDEDBLOCKS);
	private Map<String, Float> blockHeight = toFloatMap(DEF_BLOCKHEIGHT);
	private boolean canGenWhenInvisible = DEF_CAN_GEN_WHEN_INVISIBLE;
	private Set<String> excludedMobs = toSet(DEF_MODS);
	private Map<String, Float> mobInterval = toFloatMap(DEF_MOB_INTERVAL);
	private Map<String, Float> sizePerMob = toFloatMap(DEF_SIZE);
	private Map<String, Float> horseLikeMobs = toFloatMap(DEF_FOUR_LEGS);
	private Map<String, Float> spiderLikeMobs = toFloatMap(DEF_EIGHT_LEGS);
	private Map<String, String[]> customPrint = toStringArrayMap(DEF_CUSTOM_PRINT);
	private int footprintSize = DEF_FOOTPRINT_SIZE;
	private int railFlameRange = DEF_RAIL_FLAME_RANGE;
	private boolean enableBoatTrail = DEF_ENABLE_BOAT_TRAIL;
	private WorkMode swimPopLevel = DEF_SWIM_POP_LEVEL;
	private WorkMode snowDustLevel = DEF_SNOW_DUST_LEVEL;
	private WorkMode waterSplashLevel = DEF_WATER_SPLASH_LEVEL;

	public WorkMode getEnableMod() {return enableMod;}
	public float getSecPerPrint() {return secPerPrint / 10f;}
	public float getPrintLifetime() {return printLifetime;}
	public float getWatermarkLifetime() {return watermarkLifetime;}
	public int getLifeTimeAcc() {return lifeTimeAcc;}
	public float getPrintHeight() {return printHeight * 0.0625f;}
	public List<String> getApplyBlocks() {return fromSet(applyBlocks);}
	public List<String> getBlockHeight() {return fromFloatMap(blockHeight);}
	public List<String> getExcludedBlocks() {return fromSet(excludedBlocks);}
	public boolean getCanGenWhenInvisible() {return canGenWhenInvisible;}
	public List<String> getExcludedMobs() {return fromSet(excludedMobs);}
	public List<String> getSizePerMob() {return fromFloatMap(sizePerMob);}
	public List<String> getHorseLikeMobs() {return fromFloatMap(horseLikeMobs);}
	public List<String> getSpiderLikeMobs() {return fromFloatMap(spiderLikeMobs);}
	public int getWetDuration() {return wetDuration;}
	public float getWatermarkAlpha() {return watermarkAlpha / 10f;}
	public float getFootprintAlpha() {return footprintAlpha / 10f;}
	public List<String> getMobInterval() {return fromFloatMap(mobInterval);}
	public int getFootprintSize() {return footprintSize;}
	public List<String> getCustomPrint() {return fromStringArrayMap(customPrint);}
	public float getHardnessGate() {return hardnessGate;}
	public float getRailFlameRange() {return railFlameRange / 10f;}
	public boolean isEnableBoatTrail() {return enableBoatTrail;}
	public WorkMode getSwimPopLevel() {return swimPopLevel;}
	public WorkMode getSnowDustLevel() {return snowDustLevel;}
	public WorkMode getWaterSplashLevel() {return waterSplashLevel;}

	public void setEnableMod(WorkMode enableMod) {this.enableMod = enableMod;}
	public void setWetDuration(int wetDuration) {this.wetDuration = wetDuration;}
	public void setSecPerPrint(int secPerPrint) {this.secPerPrint = secPerPrint;}
	public void setPrintLifetime(float printLifetime) {this.printLifetime = printLifetime;}
	public void setWatermarkLifetime(float watermarkLifetime) {this.watermarkLifetime = watermarkLifetime;}
	public void setLifeTimeAcc(int lifeTimeAcc) {this.lifeTimeAcc = lifeTimeAcc;}
	public void setFootprintAlpha(int footprintAlpha) {this.footprintAlpha = footprintAlpha;}
	public void setWatermarkAlpha(int watermarkAlpha) {this.watermarkAlpha = watermarkAlpha;}
	public void setPrintHeight(int printHeight) {this.printHeight = printHeight;}
	public void setApplyBlocks(List<String> applyBlocks) {this.applyBlocks = toSet(applyBlocks);}
	public void setHardnessGate(float hardnessGate) {this.hardnessGate = hardnessGate;}
	public void setExcludedBlocks(List<String> excludedBlocks) {this.excludedBlocks = toSet(excludedBlocks);}
	public void setBlockHeight(List<String> blockHeight) {this.blockHeight = toFloatMap(blockHeight);}
	public void setCanGenWhenInvisible(boolean canGenWhenInvisible) {this.canGenWhenInvisible = canGenWhenInvisible;}
	public void setExcludedMobs(List<String> excludedMobs) {this.excludedMobs = toSet(excludedMobs);}
	public void setMobInterval(List<String> mobInterval) {this.mobInterval = toFloatMap(mobInterval);}
	public void setSizePerMob(List<String> sizePerMob) {this.sizePerMob = toFloatMap(sizePerMob);}
	public void setHorseLikeMobs(List<String> horseLikeMobs) {this.horseLikeMobs = toFloatMap(horseLikeMobs);}
	public void setSpiderLikeMobs(List<String> spiderLikeMobs) {this.spiderLikeMobs = toFloatMap(spiderLikeMobs);}
	public void setCustomPrint(List<String> customPrint) {this.customPrint = toStringArrayMap(customPrint);}
	public void setFootprintSize(int footprintSize) {this.footprintSize = footprintSize;}
	public void setRailFlameRange(int railFlameRange) {this.railFlameRange = railFlameRange;}
	public void setEnableBoatTrail(boolean enableBoatTrail) {this.enableBoatTrail = enableBoatTrail;}
	public void setSwimPopLevel(WorkMode swimPopLevel) {this.swimPopLevel = swimPopLevel;}
	public void setSnowDustLevel(WorkMode snowDustLevel) {this.snowDustLevel = snowDustLevel;}
	public void setWaterSplashLevel(WorkMode waterSplashLevel) {this.waterSplashLevel = waterSplashLevel;}

	/* - - - - - 内部集合裸视图（模组逻辑直接查表，避免拷贝 List） - - - - - */

	public Set<String> getApplyBlockSet() {return applyBlocks;}
	public Set<String> getExcludedBlockSet() {return excludedBlocks;}
	public Set<String> getExcludedMobSet() {return excludedMobs;}
	public Map<String, Float> getBlockHeightMap() {return blockHeight;}
	public Map<String, Float> getMobIntervalMap() {return mobInterval;}
	public Map<String, Float> getSizePerMobMap() {return sizePerMob;}
	public Map<String, Float> getHorseLikeMobsMap() {return horseLikeMobs;}
	public Map<String, Float> getSpiderLikeMobsMap() {return spiderLikeMobs;}
	public Map<String, String[]> getCustomPrintMap() {return customPrint;}

	static final WorkMode DEF_ENABLE_MOD = WorkMode.ALL;
	static final int DEF_WET_DURATION = 10;
	static final int DEF_SEC_PER_PRINT = 5;
	static final float DEF_PRINT_LIFETIME = 5.0f;
	static final float DEF_WATERMARK_LIFETIME = 5.0f;
	static final int DEF_LIFETIME_ACC = 0;
	static final int DEF_FOOTPRINT_ALPHA = 7;
	static final int DEF_WATERMARK_ALPHA = 4;
	static final int DEF_PRINT_HEIGHT = 0;
	static final float DEF_HARDNESS_GATE = 0.7f;
	static final boolean DEF_CAN_GEN_WHEN_INVISIBLE = true;
	static final int DEF_FOOTPRINT_SIZE = 5;
	static final int DEF_RAIL_FLAME_RANGE = 2;
	static final boolean DEF_ENABLE_BOAT_TRAIL = true;
	static final WorkMode DEF_SWIM_POP_LEVEL = WorkMode.ALL;
	static final WorkMode DEF_SNOW_DUST_LEVEL = WorkMode.ALL;
	static final WorkMode DEF_WATER_SPLASH_LEVEL = WorkMode.PLAYER_ONLY;
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
	static final List<String> DEF_SIZE = Arrays.asList(
			"minecraft:chicken,0.6",
			"minecraft:pig,0.8",
			"minecraft:cat,0.5",
			"minecraft:ocelot,0.5",
			"minecraft:wolf,0.6",
			"minecraft:sniffer,1.6",
			"minecraft:enderman,0.6",
			"minecraft:slime,2.0",
			"minecraft:magma_cube,2.0",
			"minecraft:creeper,0.8",
			"minecraft:iron_golem,1.2",
			"minecraft:ravager,2.0",
			"minecraft:armadillo,0.7"
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
			"minecraft:camel,2.0",
			"minecraft:sniffer,3.0",
			"minecraft:iron_golem,2.0",
			"minecraft:creeper,0.8"
	);
	static final List<String> DEF_CUSTOM_PRINT = Arrays.asList(
			"mod_id:mob_id,fileName_NoExtend"
	);

	/* - - - - - IO - - - - - */

	// serializeNulls 保证 Map<String,Float> 里“有 key 无 value”（如 horseLikeMobs 中的 “minecraft:horse”）能落盘与回读。
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	private static final Path CONFIG_PATH = PlatformUtil.PLATFORM.getConfigFolder().resolve(Client.MOD_ID + ".json");

	private Config() {}

	public static Config load() {
		Config config = new Config();
		if (Files.exists(CONFIG_PATH)) {
			try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
				JsonElement rootEl = GSON.fromJson(reader, JsonElement.class);
				Config loaded = null;
				if (rootEl != null && rootEl.isJsonObject()) {
					JsonObject root = rootEl.getAsJsonObject();
					// 旧版本 List<String> 格式兼容：字段在磁盘上仍是数组时，按行而解析入对象。
					migrateLegacyFloatMap(root, "blockHeight");
					migrateLegacyFloatMap(root, "mobInterval");
					migrateLegacyFloatMap(root, "sizePerMob");
					migrateLegacyFloatMap(root, "horseLikeMobs");
					migrateLegacyFloatMap(root, "spiderLikeMobs");
					migrateLegacyStringArrayMap(root, "customPrint");
					loaded = GSON.fromJson(root, Config.class);
				} else if (rootEl != null) {
					loaded = GSON.fromJson(rootEl, Config.class);
				}
				if (loaded != null) {
					config = loaded;
				}
			} catch (IOException | JsonParseException e) {
				Client.LOGGER.error("Failed to read config file: {}, using current/default config", CONFIG_PATH, e);
			}
		} else {
			config.save();
		}
		return config;
	}

	public void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			Client.LOGGER.error("Failed to write config file: {}", CONFIG_PATH, e);
		}
	}

	/* - - - - - List <-> Set/Map - - - - - */

	private static Set<String> toSet(List<String> list) {
		Set<String> set = new LinkedHashSet<>();
		if (list != null) {
			for (String s : list) {
				if (s != null && !s.isEmpty()) set.add(s);
			}
		}
		return set;
	}

	private static List<String> fromSet(Set<String> set) {
		return new ArrayList<>(set);
	}

	/** 解析 "key,value" 行；无逗号或 value 非法 → 存入 null（保留 key，由消费方处理默认）。 */
	private static Map<String, Float> toFloatMap(List<String> list) {
		Map<String, Float> map = new LinkedHashMap<>();
		if (list == null) return map;
		for (String s : list) {
			if (s == null || s.isEmpty()) continue;
			int idx = s.indexOf(',');
			if (idx < 0) {
				map.put(s, null);
			} else {
				String key = s.substring(0, idx);
				Float value = parseFloat(s.substring(idx + 1).trim());
				map.put(key, value);
			}
		}
		return map;
	}

	private static List<String> fromFloatMap(Map<String, Float> map) {
		List<String> list = new ArrayList<>(map.size());
		for (Map.Entry<String, Float> e : map.entrySet()) {
			if (e.getValue() == null) list.add(e.getKey());
			else list.add(e.getKey() + "," + e.getValue());
		}
		return list;
	}

	/** 解析 "key,v1,v2,..." 行；无逗号的行直接丢弃（customPrint 至少要有 1 个 value）。 */
	private static Map<String, String[]> toStringArrayMap(List<String> list) {
		Map<String, String[]> map = new LinkedHashMap<>();
		if (list == null) return map;
		for (String s : list) {
			if (s == null || s.isEmpty()) continue;
			int idx = s.indexOf(',');
			if (idx < 0) continue;
			map.putIfAbsent(s.substring(0, idx), s.substring(idx + 1).split(",", -1));
		}
		return map;
	}

	private static List<String> fromStringArrayMap(Map<String, String[]> map) {
		List<String> list = new ArrayList<>(map.size());
		for (Map.Entry<String, String[]> e : map.entrySet()) {
			list.add(e.getKey() + "," + String.join(",", e.getValue()));
		}
		return list;
	}

	private static Float parseFloat(String raw) {
		try { return Float.parseFloat(raw); }
		catch (NumberFormatException e) { return null; }
	}

	/* - - - - - 旧版 List<String> 磁盘格式→ Map 对象兼容 - - - - - */

	/**
	 * 旧版中 {@code blockHeight / mobInterval / sizePerMob / horseLikeMobs / spiderLikeMobs}
	 * 在磁盘上写为 {@code ["key,value", ...]}，需转为 Gson 可直接反序列化的
	 * {@code {"key": value}} 对象；无逗号或 value 非法时写入 JsonNull，与 {@link #toFloatMap(List)} 行为一致。
	 */
	private static void migrateLegacyFloatMap(JsonObject root, String key) {
		JsonElement el = root.get(key);
		if (el == null || !el.isJsonArray()) return;
		JsonObject obj = new JsonObject();
		for (JsonElement item : el.getAsJsonArray()) {
			if (!item.isJsonPrimitive()) continue;
			String line = item.getAsString();
			if (line.isEmpty()) continue;
			int idx = line.indexOf(',');
			if (idx < 0) {
				obj.add(line, JsonNull.INSTANCE);
			} else {
				String k = line.substring(0, idx);
				Float v = parseFloat(line.substring(idx + 1).trim());
				if (v == null) obj.add(k, JsonNull.INSTANCE);
				else obj.addProperty(k, v);
			}
		}
		root.add(key, obj);
	}

	/**
	 * 旧版 {@code customPrint} 写为 {@code ["key,v1,v2,...", ...]}，转为 {@code {"key": ["v1","v2",...]}}；
	 * 无逗号的行直接丢弃，与 {@link #toStringArrayMap(List)} 行为一致。
	 */
	private static void migrateLegacyStringArrayMap(JsonObject root, String key) {
		JsonElement el = root.get(key);
		if (el == null || !el.isJsonArray()) return;
		JsonObject obj = new JsonObject();
		for (JsonElement item : el.getAsJsonArray()) {
			if (!item.isJsonPrimitive()) continue;
			String line = item.getAsString();
			if (line.isEmpty()) continue;
			int idx = line.indexOf(',');
			if (idx < 0) continue;
			String k = line.substring(0, idx);
			if (obj.has(k)) continue;
			JsonArray arr = new JsonArray();
			for (String v : line.substring(idx + 1).split(",", -1)) arr.add(v);
			obj.add(k, arr);
		}
		root.add(key, obj);
	}
}
