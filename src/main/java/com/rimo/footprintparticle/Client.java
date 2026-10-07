package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WaterSplashParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 仅客户端模组的共享入口：MOD_ID / 日志 / 配置单例 / 4 个粒子类型实例。
 *
 * <p>粒子类型对象在此仅 <em>构造</em>，真正的注册（写入 BuiltInRegistries.PARTICLE_TYPE）留给
 * {@code loaders.<平台>.Platform}——Fabric 用 {@code Registry.register}（走 {@code onInitializeClient}），
 * NeoForge 用 {@code RegisterEvent.register}（挂在 {@code @EventBusSubscriber(value = Dist.CLIENT)} 里）；
 * 两边再通过 lambda 引用同一批 {@code public static final} 实例。</p>
 */
public class Client {
	public static final String MOD_ID = "footprintparticle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Only assign in applyConfig()
	public static volatile Config CONFIG;

	// Extract CONFIG.List<String> to Map to improved lookup speed.
	public static volatile Map<String, Float> SIZE_PER_MOB = Map.of();
	public static volatile Map<String, Float> MOB_INTERVAL = Map.of();
	public static volatile Map<String, Float> HORSE_LIKE_MOBS = Map.of();
	public static volatile Map<String, Float> SPIDER_LIKE_MOBS = Map.of();
	public static volatile Map<String, String[]> CUSTOM_PRINT = Map.of();
	public static volatile Map<String, Float> BLOCK_HEIGHT = Map.of();
	public static volatile Map<String, Float> BLOCK_HEIGHT_BY_TAG = Map.of();
	public static volatile Set<String> APPLY_BLOCKS = Set.of();
	public static volatile Set<String> EXCLUDED_BLOCKS = Set.of();
	public static volatile Set<String> EXCLUDED_MOBS = Set.of();

	// Our Particles...
	public static final FootprintParticleType FOOTPRINT = new FootprintParticleType(true);
	public static final WatermarkParticleType WATERMARK = new WatermarkParticleType(true);
	public static final SnowDustParticleType SNOWDUST = new SnowDustParticleType(true);
	public static final WaterSplashParticleType WATERSPLASH = new WaterSplashParticleType(true);

	static {
		ConfigHolder<Config> holder = AutoConfig.register(Config.class, GsonConfigSerializer::new);
		// 配置屏保存：GUI 通过反射把值原地写回同一个实例，随后 save() 回调这里，需要重建索引。
		holder.registerSaveListener((h, config) -> {
			applyConfig(config);
			return InteractionResult.PASS;
		});
		// 读盘（load()）时实例被整个替换，引用与索引都要跟着换。
		holder.registerLoadListener((h, config) -> {
			applyConfig(config);
			return InteractionResult.PASS;
		});
		applyConfig(holder.getConfig());
	}

	public static void init() {
		// 平台差异化注册在 loaders.<平台>.Platform 里完成；此处留空作为共享初始化钩子。
	}

	/**
	 * Refresh CONFIG reference and Maps.
	 */
	public static void applyConfig(Config config) {
		if (config == null)
			return;
		CONFIG = config;

		Map<String, Float> size = new HashMap<>();
		for (String line : lines(config.getSizePerMob())) {
			String[] parts = line.split(",");
			Float value = parts.length > 1 ? parse(parts[1]) : null;
			if (value != null)
				size.merge(parts[0], value, (a, b) -> a * b);
		}
		SIZE_PER_MOB = Map.copyOf(size);

		MOB_INTERVAL = Map.copyOf(values(config.getMobInterval()));
		HORSE_LIKE_MOBS = Map.copyOf(offsets(config.getHorseLikeMobs(), 0.75f));
		SPIDER_LIKE_MOBS = Map.copyOf(offsets(config.getSpiderLikeMobs(), 0.9f));

		Map<String, String[]> custom = new HashMap<>();
		for (String line : lines(config.getCustomPrint())) {
			String[] parts = line.split(",");
			if (parts.length < 2)
				continue;
			custom.putIfAbsent(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
		}
		CUSTOM_PRINT = Map.copyOf(custom);

		Map<String, Float> height = new HashMap<>();
		Map<String, Float> heightByTag = new HashMap<>();
		for (String line : lines(config.getBlockHeight())) {
			String[] parts = line.split(",");
			Float value = parts.length > 1 ? parse(parts[1]) : null;
			if (value == null)
				continue;
			if (parts[0].charAt(0) == '#')
				heightByTag.putIfAbsent(parts[0], value);
			else
				height.putIfAbsent(parts[0], value);
		}
		BLOCK_HEIGHT = Map.copyOf(height);
		BLOCK_HEIGHT_BY_TAG = Map.copyOf(heightByTag);

		APPLY_BLOCKS = toSet(config.getApplyBlocks());
		EXCLUDED_BLOCKS = toSet(config.getExcludedBlocks());
		EXCLUDED_MOBS = toSet(config.getExcludedMobs());
	}

	/** 需要 {@code id,value} 两段齐全的行（间隔、高度等）；只写 ID 或数值非法的行直接丢弃。 */
	private static Map<String, Float> values(List<String> configured) {
		Map<String, Float> map = new HashMap<>();
		for (String line : lines(configured)) {
			String[] parts = line.split(",");
			Float value = parts.length > 1 ? parse(parts[1]) : null;
			if (value != null)
				map.putIfAbsent(parts[0], value);
		}
		return map;
	}

	/** 允许只写 ID 的偏移行（四足 0.75 / 多足 0.9）：缺省或非法数值时用 {@code def}，保留“命中即生效”的原语义。 */
	private static Map<String, Float> offsets(List<String> configured, float def) {
		Map<String, Float> map = new HashMap<>();
		for (String line : lines(configured)) {
			String[] parts = line.split(",");
			if (parts[0].isEmpty())
				continue;
			Float value = parts.length > 1 ? parse(parts[1]) : null;
			map.putIfAbsent(parts[0], value != null ? value : def);
		}
		return map;
	}

	/** 只供读取的过滤副本，不会改动配置对象里那个 List。 */
	private static List<String> lines(List<String> configured) {
		if (configured == null || configured.isEmpty())
			return List.of();
		List<String> copy = new ArrayList<>(configured.size());
		for (String line : configured)
			if (line != null && !line.isEmpty())
				copy.add(line);
		return copy;
	}

	private static Set<String> toSet(List<String> configured) {
		Set<String> set = new HashSet<>(lines(configured));
		return Set.copyOf(set);
	}

	private static Float parse(String value) {
		try {
			return Float.parseFloat(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
