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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
	private int genHeightGap = DEF_GEN_HEIGHT_GAP;
	private int genDistChunk = DEF_GEN_DIST_CHUNK;

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
	public float getGenHeightGap() {return genHeightGap / 10F;}
	public int getGenDistSqr() {return genDistChunk * genDistChunk * 256;}

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
	public void setGenHeightGap(int genHeightGap) {this.genHeightGap = genHeightGap;}
	public void setGenDistChunk(int genDistChunk) {this.genDistChunk = genDistChunk;}

	/* - - - Set/Map lookup - - - */
	public boolean isWhiteListBlock(String id, List<String> tags) {return isInSet(id, tags, applyBlocks);}
	public boolean isBlackListBlock(String id, List<String> tags) {return isInSet(id, tags, excludedBlocks);}
	public boolean isBlackListMob(String id, List<String> tags) {return isInSet(id, tags, excludedMobs);}
	/**
	 * @return 0F if no match found.
	 */
	public float getBlockHeight(String id, List<String> tags) {return getFloatNoNaN(id, tags, blockHeight, 0F);}
	/**
	 * @return 1F if no match found.
	 */
	public float getMobInterval(String id, List<String> tags) {return getFloatNoNaN(id, tags, mobInterval, 1F);}
	/**
	 * @return 1F if no match found.
	 */
	public float getMobSize(String id, List<String> tags) {return getFloatNoNaN(id, tags, sizePerMob, 1F);}
	/**
	 * @return {@code value} if specific id or tags matched,<br>
	 *         {@code 0.75F} if matched but no value (horse default),<br>
	 *         {@link Float#NaN} if no match found.
	 */
	public float getMobFront(String id, List<String> tags) {return getFloatOrDefault(id, tags, horseLikeMobs, 0.75F);}
	/**
	 * @return {@code value} if specific id or tags matched,<br>
	 *         {@code 0.9F} if matched but no value (spider default),<br>
	 *         {@link Float#NaN} if no match found.
	 */
	public float getMobLateral(String id, List<String> tags) {return getFloatOrDefault(id, tags, spiderLikeMobs, 0.9F);}
	public String[] getCustomTextureNames(String id, List<String> tags) {
		if (customPrint.isEmpty()) return null;
		if (customPrint.containsKey(id)) return customPrint.get(id).clone();
		for (Map.Entry<String, String[]> entry : customPrint.entrySet())
			if (tags.contains(entry.getKey())) return entry.getValue().clone();
		return null;
	}

	private static boolean isInSet(String id, List<String> ids, Set<String> set) {
		if (set.isEmpty()) return false;
		if (set.contains(id)) return true;
		for (String tag : ids)
			if (set.contains(tag)) return true;
		return false;
	}
	/**
	 * house/spider map has 'null' value that indicate default, we must resolve it.<br>
	 * To indicate 'no match found', we use {@link Float#NaN} as return.
	 */
	private static float getFloatOrDefault(String id, List<String> ids, Map<String, Float> map, float defVal) {
		if (map.isEmpty()) return Float.NaN;
		Float value = map.containsKey(id) ? map.get(id) : Float.valueOf(Float.NaN);
		if (value == null) return defVal;
		if (! Float.isNaN(value) || ids.isEmpty()) return value;
		for (Map.Entry<String, Float> entry : map.entrySet()) {
			if (ids.contains(entry.getKey())) {
				value = entry.getValue();
				return value == null ? defVal : value;
			}
		}
		return Float.NaN;
	}
	private static float getFloatNoNaN(String id, List<String> ids, Map<String, Float> map, float defVal) {
		float value = getFloatOrDefault(id, ids, map, defVal);
		return Float.isNaN(value) ? defVal : value;
	}

	/* - - - Default value - - - */
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
	static final int DEF_GEN_HEIGHT_GAP = 5;
	static final int DEF_GEN_DIST_CHUNK = 4;
	static final List<String> DEF_APPLYBLOCKS = Arrays.asList(
			"#minecraft:wool",
			"#minecraft:wool_slabs",
			"#minecraft:wool_stairs"
	);
	static final List<String> DEF_BLOCKHEIGHT = Arrays.asList(
			"minecraft:snow,0.125",
			"minecraft:soul_sand,0.125",
			"minecraft:mud,0.125",
			"#minecraft:pressure_plates,0.0625"  // raise for water print, though it disabled in excludedList.
	);
	static final List<String> DEF_EXCLUDEDBLOCKS = Arrays.asList(
			"#minecraft:beehives",
			"#minecraft:pressure_plates",
			"minecraft:brewing_stand",
			"minecraft:composter",
			"minecraft:tnt",
			"minecraft:target",
			"minecraft:daylight_detector",
			"minecraft:repeater",
			"minecraft:comparator",
			"minecraft:redstone_lamp",
			"minecraft:sea_lantern",
			"minecraft:glowstone",
			"minecraft:magma_block",
			"minecraft:big_dripleaf",
			"minecraft:ochre_froglight",
			"minecraft:verdant_froglight",
			"minecraft:pearlescent_froglight"
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
			"minecraft:rabbit,0.7",
			"minecraft:sniffer,1.6",
			"minecraft:enderman,0.6",
			"minecraft:slime,2.0",
			"minecraft:magma_cube,2.0",
			"minecraft:creeper,0.8",
			"minecraft:iron_golem,1.2",
			"minecraft:ravager,2.0",
			"minecraft:armadillo,0.7",
			"minecraft:copper_golem,0.8",
			"minecraft:sulfur_cube,2.0"
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
			"minecraft:creeper,0.3",
			"minecraft:camel_husk"
	);
	static final List<String> DEF_EIGHT_LEGS = Arrays.asList(
			"minecraft:spider",
			"minecraft:cave_spider",
			"minecraft:rabbit,0.3",
			"minecraft:camel,0.3",
			"minecraft:sniffer,0.3",
			"minecraft:iron_golem,0.3",
			"minecraft:ravager,0.3",
			"minecraft:breeze,0.1",
			"minecraft:camel_husk,0.3",
			"minecraft:slime,0.3",
			"minecraft:magma_cube,0.3",
			"minecraft:sulfur_cube,0.3"
	);
	static final List<String> DEF_MOB_INTERVAL = Arrays.asList(
			"minecraft:spider,0.5",
			"minecraft:cave_spider,0.5",
			"minecraft:camel,2.0",
			"minecraft:sniffer,3.0",
			"minecraft:iron_golem,2.0",
			"minecraft:creeper,0.8",
			"minecraft:creaking,1.6",
			"minecraft:breeze,0.3",
			"minecraft:camel_husk,2.0"
	);
	static final List<String> DEF_CUSTOM_PRINT = Arrays.asList(
			"mod_id:mob_id,fileName_NoExtend"
	);

	/* - - - - - IO - - - - - */

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()  // make file readable.
			.serializeNulls()  // make null field can be written/loaded.
			.create();
	// Default config path. May '.minecraft/config/footprintparticle.json'.
	private static final Path CONFIG_PATH = PlatformUtil.PLATFORM.getConfigFolder().resolve(Client.MOD_ID + ".json");
	private static final Config INSTANCE = Config.load();

	private Config() {}

	public static Config getInstance() {
		return INSTANCE;
	}

	private static Config load() {
		Config config = new Config();
		if (Files.exists(CONFIG_PATH)) {
			try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
				JsonElement rootEl = GSON.fromJson(reader, JsonElement.class);
				Config loaded = null;
				if (rootEl != null && rootEl.isJsonObject()) {
					JsonObject root = rootEl.getAsJsonObject();
					// Transform old List<String> to Map for compat.
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
				config.fillDefaults();
				config.sanitize();
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
			// Serialize before opening the file, so a failure cannot truncate the previous config.
			String json = GSON.toJson(this);
			Files.createDirectories(CONFIG_PATH.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
				writer.write(json);
			}
		} catch (IOException | JsonParseException | IllegalArgumentException e) {
			Client.LOGGER.error("Failed to write config file: {}", CONFIG_PATH, e);
		}
	}

	// Codes below are vibe coding by CoderCN...
	/* - - - - - List <-> Set/Map - - - - - */

	/** The editor rejects blank lines, so the list can be copied as-is. */
	private static Set<String> toSet(List<String> list) {
		return list == null ? new LinkedHashSet<>() : new LinkedHashSet<>(list);
	}

	private static List<String> fromSet(Set<String> set) {
		return new ArrayList<>(set);
	}

	/**
	 * Parse "key,value" lines into a map. A bare "key" keeps the key with a null value (consumer default), which is
	 *  only accepted by the offset lists {@code horseLikeMobs / spiderLikeMobs}; other lists require a value and the
	 *  editor rejects a value-less line. The editor rejects blank lines before saving.
	 */
	private static Map<String, Float> toFloatMap(List<String> list) {
		Map<String, Float> map = new LinkedHashMap<>();
		if (list == null) return map;
		for (String line : list) {
			if (line == null) continue;
			int idx = line.indexOf(',');
			if (idx < 0) {
				map.put(line, null);
			} else {
				map.put(line.substring(0, idx), parseFloat(line.substring(idx + 1).trim()));
			}
		}
		return map;
	}

	private static List<String> fromFloatMap(Map<String, Float> map) {
		List<String> list = new ArrayList<>(map.size());
		for (Map.Entry<String, Float> e : map.entrySet()) {
			Float value = e.getValue();
			if (value == null || !Float.isFinite(value)) list.add(e.getKey());
			else list.add(e.getKey() + "," + value);
		}
		return list;
	}

	/**
	 * Parse "key,v1,v2,..." lines into a map. A line without a comma is discarded and a repeated key keeps its
	 *  first line; the editor rejects both cases before saving.
	 */
	private static Map<String, String[]> toStringArrayMap(List<String> list) {
		Map<String, String[]> map = new LinkedHashMap<>();
		if (list == null) return map;
		for (String line : list) {
			if (line == null) continue;
			int idx = line.indexOf(',');
			if (idx < 0) continue;
			map.putIfAbsent(line.substring(0, idx), line.substring(idx + 1).split(",", -1));
		}
		return map;
	}

	private static List<String> fromStringArrayMap(Map<String, String[]> map) {
		List<String> list = new ArrayList<>(map.size());
		for (Map.Entry<String, String[]> e : map.entrySet()) {
			String[] values = e.getValue();
			if (values == null) continue;
			list.add(e.getKey() + "," + String.join(",", values));
		}
		return list;
	}

	private static Float parseFloat(String raw) {
		try {
			float value = Float.parseFloat(raw);
			return Float.isFinite(value) ? value : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/* - - - - - Legacy List<String> -> Map migration - - - - - */

	/**
	 * Migrate legacy {@code blockHeight / mobInterval / sizePerMob / horseLikeMobs / spiderLikeMobs} values from
	 *  {@code ["key,value", ...]} to a Gson-deserializable {@code {"key": value}} object. A missing comma or an
	 *  invalid value becomes {@link JsonNull}, matching the leniency of {@link #toFloatMap(List)}.
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
	 * Old {@code customPrint} written as {@code ["key,v1,v2,...", ...]} needs to be transformed to {@code {"key": ["v1","v2",...]}};
	 * lines without a comma are discarded, matching {@link #toStringArrayMap(List)}.
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

	/**
	 * Restore reference fields that Gson left {@code null} (unreadable old enum values, explicit nulls or missing
	 *  keys) from a fresh default instance, so getters never return null; Cloth's EnumSelector cannot handle a null
	 *  current value. The next {@link #save()} persists the repaired values.<br>
	 * <br>
	 * Primitives are skipped by Gson on missing/null keys, so their initializers already keep the current defaults.<br>
	 */
	private void fillDefaults() {
		Config defaults = new Config();
		for (Field field : Config.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) continue;
			try {
				if (field.get(this) == null) field.set(this, field.get(defaults));
			} catch (ReflectiveOperationException e) {
				Client.LOGGER.error("Failed to restore default for config field {}", field.getName(), e);
			}
		}
	}

	/**
	 * Drop values that cannot be represented in JSON or used by consumers: empty keys, value-less entries of
	 *  {@code blockHeight / mobInterval / sizePerMob} (their consumers have no fallback), non-finite floats, empty
	 *  customPrint entries, and null/empty set entries. Otherwise an entry the editor flags (e.g. a blank sprite
	 *  name loaded from JSON) would keep the save button disabled until the user removed it manually.<br>
	 * <br>
	 * {@code horseLikeMobs / spiderLikeMobs} keep a null value as a marker for their default offset.
	 */
	private void sanitize() {
		for (Map<String, Float> map : Arrays.asList(blockHeight, mobInterval, sizePerMob)) {
			map.keySet().removeIf(String::isEmpty);
			map.values().removeIf(value -> value == null || !Float.isFinite(value));
		}
		for (Map<String, Float> map : Arrays.asList(horseLikeMobs, spiderLikeMobs)) {
			map.keySet().removeIf(String::isEmpty);
			map.replaceAll((key, value) -> value == null || Float.isFinite(value) ? value : null);
		}

		customPrint.keySet().removeIf(String::isEmpty);
		customPrint.replaceAll((key, values) -> values == null
				? null
				: Arrays.stream(values).filter(value -> value != null && !value.isEmpty()).toArray(String[]::new));
		customPrint.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().length == 0);
		applyBlocks.removeIf(value -> value == null || value.isEmpty());
		excludedBlocks.removeIf(value -> value == null || value.isEmpty());
		excludedMobs.removeIf(value -> value == null || value.isEmpty());
	}
}
