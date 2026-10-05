//? if fabric {
package com.rimo.footprintparticle.loaders.fabric;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.particle.FootprintParticle;
import com.rimo.footprintparticle.particle.SnowDustParticle;
import com.rimo.footprintparticle.particle.WaterSplashParticle;
import com.rimo.footprintparticle.particle.WatermarkParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Fabric 侧入口。 {@code fabric.mod.json} 的 {@code client} entrypoint 指向本类。
 *
 * <p>Sprite 枚举能力由 mixin {@code ParticleSpriteSetAccessor} 走 SpongePowered Mixin 双端统一提供，
 * 本类不再需要 {@code <clinit>} 注册 {@code IPlatform} 实现。</p>
 */
public class Platform implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 粒子类型注册：写入原版 BuiltInRegistries.PARTICLE_TYPE（Fabric 走副作用式 Registry.register）。
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":footprint", Client.FOOTPRINT);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":watermark", Client.WATERMARK);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":snowdust", Client.SNOWDUST);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Client.MOD_ID + ":watersplash", Client.WATERSPLASH);

		Client.init();

		ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();
		registry.register(Client.FOOTPRINT, FootprintParticle.DefaultFactory::new);
		registry.register(Client.WATERMARK, WatermarkParticle.DefaultFactory::new);
		registry.register(Client.SNOWDUST, SnowDustParticle.DefaultFactory::new);
		registry.register(Client.WATERSPLASH, WaterSplashParticle.DefaultFactory::new);
	}
}
//? }
