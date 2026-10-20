//? if fabric {
package com.rimo.footprintparticle.loaders.fabric;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.particle.FppParticleRegistry;
import com.rimo.footprintparticle.PlatformUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
//? if <= 1.19.2 {
/*import com.rimo.footprintparticle.VersionUtil;
import com.rimo.footprintparticle.particle.FppSpriteAtlas;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.resources.ResourceLocation;
*///? }

import java.nio.file.Path;

/**
 * Fabric 侧入口。 {@code fabric.mod.json} 的 {@code client} entrypoint 指向本类。
 *
 * <p>粒子已完全自托管（见 {@link FppParticleRegistry} 与共享 {@code ParticleEngineMixin}）：不再把
 * 4 个 {@code ParticleType} 注册进原版 {@code BuiltInRegistries.PARTICLE_TYPE}——那张表会被
 * {@code fabric-registry-sync-v0} 同步给连接方，未装本模组的客户端会因此被踢线。也不再走
 * {@code ParticleProviderRegistry} 注册 provider——provider 现在由我们的私有表持有，在 {@code makeParticle}
 * HEAD 拦截时直接使用。</p>
 *
 * <p>唯一还需要的加载器接线是贴图缝合：&le;1.19.2 的粒子图集缝合是注册驱动的，未注册类型的贴图不会被
 * 自动缝进去，故这里用 {@code ClientSpriteRegistryCallback} 手动 addSprite；&ge;1.20.1 是资源驱动的
 * （原版枚举所有 {@code particles/*.json} 缝合），无需手动登记。</p>
 */
public class Platform implements ClientModInitializer {
	static {
		PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
			public boolean isModLoaded(String id) {
				return FabricLoader.getInstance().isModLoaded(id);
			}

			public Path getConfigFolder() {
				return FabricLoader.getInstance().getConfigDir();
			}
		};
	}

	@Override
	public void onInitializeClient() {
		Client.init();

		//? if <= 1.19.2 {
		/*// ≤1.19.2：把本模组自定义贴图手动缝进 particles 图集（≥1.20.1 资源驱动，无需此步）。
		ClientSpriteRegistryCallback.event(VersionUtil.getId("minecraft", "textures/atlas/particles.png"))
				.register((atlas, registry) -> FppSpriteAtlas.forEachStitchTexture(FppParticleRegistry.particlePaths(),
						(namespace, path) -> registry.register(new ResourceLocation(namespace, path))));
		*///? }
	}
}
//? }
