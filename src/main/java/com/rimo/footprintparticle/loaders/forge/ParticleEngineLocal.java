//? if forge {
/*package com.rimo.footprintparticle.loaders.forge;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/^*
 * 由 {@link com.rimo.footprintparticle.loaders.forge.mixin.ParticleEngineMixin} 混入到原版
 * {@code ParticleEngine} 上的本地注册入口。
 *
 * <p>Forge 的 {@code RegisterParticleProvidersEvent.registerSpriteSet} 依赖原版
 * {@code minecraft:particle_type} 表来解析 provider，纯客户端注册的条目在登录后会被同步表重建洗掉，
 * 因此粒子生不出来。本接口提供一条绕过原版注册表的通道：直接按 {@link ResourceLocation}
 * 把 provider 与一个 {@code MutableSpriteSet} 塞进 {@code ParticleEngine} 内部的两张表。</p>
 *
 * <p>注意：本接口必须位于 mixin 配置声明的 {@code package}（{@code ...loaders.forge.mixin}）<b>之外</b>。
 * 否则它会被 Mixin 归入受管包，{@link Platform} 从包外直接引用它将触发
 * {@code IllegalClassLoadError: ... cannot be referenced directly}。</p>
 ^/
public interface ParticleEngineLocal {
	/^*
	 * 以本地方式注册一个粒子 provider。
	 *
	 * <p>内部会新建一个 {@link ParticleEngine.MutableSpriteSet} 放进 {@code spriteSets}（键为 {@code id}），
	 * 原版资源重载随后会依据 {@code assets/<ns>/particles/<path>.json} 描述文件把 sprite 回填进该集合
	 * ——这也保留了自定义印花（用户在资源包里改 footprint.json 增加贴图）的能力。provider 则由
	 * {@code providerFactory} 基于该 sprite 集合现场构造，写入 {@code providers}。</p>
	 *
	 * @param id             粒子在 {@link ForgeParticleRegistry} 私有表中的 RL
	 * @param providerFactory 以 sprite 集合构造 provider 的工厂（各粒子的 {@code DefaultFactory::new}）
	 ^/
	<O extends ParticleOptions> void fpp$registerLocal(
			ResourceLocation id,
			Function<ParticleEngine.MutableSpriteSet, ? extends ParticleProvider<O>> providerFactory);
}
*///? }
