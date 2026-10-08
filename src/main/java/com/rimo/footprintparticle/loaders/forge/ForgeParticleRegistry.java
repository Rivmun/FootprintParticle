//? if forge {
/*package com.rimo.footprintparticle.loaders.forge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.VersionUtil;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/^*
 * Forge 侧私有、不同步的粒子类型登记表。
 *
 * <p>为什么不复用原版 {@code minecraft:particle_type}：Forge 把该表当作 server→client 同步表，
 * 仅在 {@code Dist=CLIENT} 单方面注册的条目在登录后会被重建立起来，导致运行期
 * {@code ParticleEngine.makeParticle} 里 {@code Registries.PARTICLE_TYPE.getKey(type)} 解析不到、
 * provider 命中失败，粒子生不出来（NeoForge/Fabric 无此约束，故不能照搬它们的注册方式）。</p>
 *
 * <p>思路源自 Visuality(1.20-forge)：把这 4 个纯客户端 {@code ParticleType} 存放在一张<b>不进原版同步表</b>
 * 的私有映射里，运行期由 {@link com.rimo.footprintparticle.loaders.forge.mixin.ParticleEngineMixin} 在
 * {@code makeParticle} 头的 {@code @Inject(cancellable)} 里先查本表取 {@link ResourceLocation}，
 * 从而让 {@code providers.get(rl)} 命中 {@link com.rimo.footprintparticle.loaders.forge.Platform} 注入的 provider。</p>
 *
 * <p>用自管理的 {@link IdentityHashMap} 而非 Forge 的 {@code IForgeRegistry}：本模组只在客户端 tick 里自发粒子，
 * 不需要 {@code /particle} 命令、注册事件或存档，私有映射即可完全满足"不同步、不进原版表"的目标，
 * 同时避开 1.20.1 自定义注册表 builder API 的版本差异。</p>
 ^/
public final class ForgeParticleRegistry {
	// RL 与 resources 里 assets/footprintparticle/particles/<name>.json 一一对应，命名空间 footprintparticle。
	public static final ResourceLocation FOOTPRINT   = VersionUtil.getId("footprint");
	public static final ResourceLocation WATERMARK   = VersionUtil.getId("watermark");
	public static final ResourceLocation SNOWDUST    = VersionUtil.getId("snowdust");
	public static final ResourceLocation WATERSPLASH = VersionUtil.getId("watersplash");

	// 用标识（==）而非 equals 比较粒子对象：我们的类型都是可变单例，hashCode 不可靠。
	private static final Map<ParticleType<?>, ResourceLocation> TYPE_TO_ID = new IdentityHashMap<>();

	// id -> 由 ParticleEngineMixin 注入引擎内部的那个空 MutableSpriteSet。≤1.19.2 forge 的粒子图集烘焙不会
	// 自动回填未注册类型的 set，需由 ForgeParticleAtlas 在 TextureStitchEvent 里手动 setSprites（见该类）。
	private static final Map<ResourceLocation, ParticleEngine.MutableSpriteSet> SPRITE_SETS = new HashMap<>();

	static {
		register(Client.FOOTPRINT, FOOTPRINT);
		register(Client.WATERMARK, WATERMARK);
		register(Client.SNOWDUST, SNOWDUST);
		register(Client.WATERSPLASH, WATERSPLASH);
	}

	private ForgeParticleRegistry() {
	}

	private static void register(ParticleType<?> type, ResourceLocation id) {
		TYPE_TO_ID.put(type, id);
	}

	/^*
	 * 反查一个粒子类型在本私有表中的 {@link ResourceLocation}；供 {@code makeParticle} 的前置注入判定
	 * “这个粒子是否归我们管”。不在本表时返回 null（即不是我们的粒子，交回原版逻辑）。
	 ^/
	public static ResourceLocation keyOf(ParticleType<?> type) {
		return TYPE_TO_ID.get(type);
	}

	/^* 本模组全部私有粒子 id（与 resources 里 assets/footprintparticle/particles/^.json 一一对应）。 ^/
	public static List<ResourceLocation> localIds() {
		return Arrays.asList(FOOTPRINT, WATERMARK, SNOWDUST, WATERSPLASH);
	}

	/^* 记录注入引擎的 MutableSpriteSet，供图集缝合回填。仅在客户端调用。 ^/
	public static void putSpriteSet(ResourceLocation id, ParticleEngine.MutableSpriteSet set) {
		SPRITE_SETS.put(id, set);
	}

	/^* 取回之前注入的 MutableSpriteSet；不存在时返回 null。 ^/
	public static ParticleEngine.MutableSpriteSet spriteSetOf(ResourceLocation id) {
		return SPRITE_SETS.get(id);
	}
}
*///? }
