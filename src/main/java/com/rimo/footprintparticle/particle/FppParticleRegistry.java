package com.rimo.footprintparticle.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 跨加载器共享的<b>自托管</b>粒子登记表（位于 root 包，不再属于 loaders 包）。
 *
 * <p>本模组是纯客户端粒子模组：4 个粒子类型<em>不</em>注册进原版 {@code minecraft:particle_type}
 * （那是被 Fabric registry-sync / NeoForge 注册表同步下发给连接方的同步表，未装模组的客户端会因“存在
 * 未注册的粒子类型”被踢线）。取而代之，本类维护一张私有 {@link IdentityHashMap}：粒子类型对象 →
 * 我们自己的 {@link ParticleProvider}（其内部持有 {@link FppSpriteSet}）。运行期由共享
 * {@link com.rimo.footprintparticle.mixin.ParticleEngineMixin} 在 {@code ParticleEngine.makeParticle} 的
 * HEAD 拦截：命中本表就用我们的 provider 直接造粒子并取消原方法，否则原样交回 vanilla。</p>
 *
 * <p>用 {@link IdentityHashMap} 而非 {@code equals}：我们的粒子类型都是可变单例（同时充当
 * {@code ParticleOptions}，{@code getType()} 返回自身），按对象标识查最稳。</p>
 *
 * <p>provider 与 sprite 集合在类初始化时一次性构造，与加载器/初始化顺序无关——mixin 首次查用即触发本类
 * 加载。sprite 的实际贴图由 {@link FppSpriteAtlas} 现取现给（见 {@link FppSpriteSet}）。</p>
 */
public final class FppParticleRegistry {
	// 粒子类型对象 -> 我们的 provider。用标识比较，见类注释。
	private static final Map<ParticleType<?>, ParticleProvider<?>> PROVIDERS = new IdentityHashMap<>();
	// 本模组全部粒子 path（与 resources 里 assets/footprintparticle/particles/<path>.json 一一对应）。
	private static final List<String> PATHS = new ArrayList<>();

	private FppParticleRegistry() {
	}

	public static void register(ParticleType<?> type, String path, Function<SpriteSet, ? extends ParticleProvider<?>> factory) {
		FppSpriteSet spriteSet = new FppSpriteSet(() -> FppSpriteAtlas.resolve(path));
		PROVIDERS.put(type, factory.apply(spriteSet));
		PATHS.add(path);
	}

	/** {@code makeParticle} 拦截时反查：该粒子类型是否归我们管、对应的 provider 是什么；不归我们管返回 null。 */
	public static ParticleProvider<?> providerOf(ParticleType<?> type) {
		return PROVIDERS.get(type);
	}

	/** 本模组全部粒子 path，供各加载器在 ≤1.19.2 图集缝合前枚举贴图 addSprite（见 {@link FppSpriteAtlas#forEachStitchTexture}）。 */
	public static List<String> particlePaths() {
		return PATHS;
	}
}
