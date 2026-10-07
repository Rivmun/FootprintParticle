//? if fabric {
package com.rimo.footprintparticle.loaders.fabric;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.particle.FootprintParticle;
import com.rimo.footprintparticle.particle.SnowDustParticle;
import com.rimo.footprintparticle.particle.WaterSplashParticle;
import com.rimo.footprintparticle.particle.WatermarkParticle;
import net.fabricmc.api.ClientModInitializer;
//~ if < 26.1 'FabricSpriteSet' -> 'FabricSpriteProvider'
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
//~ if < 26.1 'ParticleProviderRegistry' -> 'ParticleFactoryRegistry'
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
//? if > 1.19.2
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

/**
 * Fabric 侧入口。 {@code fabric.mod.json} 的 {@code client} entrypoint 指向本类。
 *
 * <p>{@code <clinit>} 早于 {@link #onInitializeClient()}——因此当 {@code Client} 首次被引用时（
 * 比如 mixin 里读 {@code Client.CONFIG}），{@code PlatformUtil.PLATFORM} 已就位。</p>
 *
 * <p>Fabric-api 将 {@code SpriteSet} 实现包装为 {@code FabricSpriteSetImpl}，它 implements
 * 官方 {@code FabricSpriteSet}，直接强转即可拿 {@code getSprites()}；不需要 mixin。</p>
 */
public class Platform implements ClientModInitializer {
	static {
		PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
			@Override
			public List<TextureAtlasSprite> getSprites(SpriteSet spriteSet) {
				// Fabric-api 的 SpriteSet 实现同时是 FabricSpriteSet，直接强转拿全部 sprite。
				//~ if < 26.1 'FabricSpriteSet' -> 'FabricSpriteProvider'
				return ((FabricSpriteSet) spriteSet).getSprites();
			}

			@Override
			public boolean isModLoaded(String id) {
				return FabricLoader.getInstance().isModLoaded(id);
			}
		};
	}

	@Override
	public void onInitializeClient() {
		// 粒子类型注册：写入原版 BuiltInRegistries.PARTICLE_TYPE（Fabric 走副作用式 Registry.register）。
		//~ if < 1.20.1 'BuiltInRegistries.PARTICLE_TYPE' -> 'Registry.PARTICLE_TYPE' {
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":footprint", Client.FOOTPRINT);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":watermark", Client.WATERMARK);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":snowdust", Client.SNOWDUST);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":watersplash", Client.WATERSPLASH);
		//~ }

		Client.init();

		//~ if < 26.1 'ParticleProviderRegistry' -> 'ParticleFactoryRegistry'
		ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();
		registry.register(Client.FOOTPRINT, FootprintParticle.DefaultFactory::new);
		registry.register(Client.WATERMARK, WatermarkParticle.DefaultFactory::new);
		registry.register(Client.SNOWDUST, SnowDustParticle.DefaultFactory::new);
		registry.register(Client.WATERSPLASH, WaterSplashParticle.DefaultFactory::new);
	}
}
//? }
