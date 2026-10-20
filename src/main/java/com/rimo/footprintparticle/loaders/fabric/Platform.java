//? if fabric {
package com.rimo.footprintparticle.loaders.fabric;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
//? if <= 1.19.2 {
/*import com.rimo.footprintparticle.VersionUtil;
import com.rimo.footprintparticle.particle.FppParticleRegistry;
import com.rimo.footprintparticle.particle.FppSpriteAtlas;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.resources.ResourceLocation;
*///? }

import java.nio.file.Path;

/**
 * Fabric client entry point: the {@code client} entrypoint in {@code fabric.mod.json}.<br>
 * Runs {@link Client#init()} and, on 1.19.2 and older, registers our textures for stitching.
 *
 * @see com.rimo.footprintparticle.particle.FppParticleRegistry
 * @see com.rimo.footprintparticle.particle.FppSpriteAtlas
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
		/*// 1.19.2 and older: add our textures to the particle atlas before stitching.
		ClientSpriteRegistryCallback.event(VersionUtil.getId("minecraft", "textures/atlas/particles.png"))
				.register((atlas, registry) -> FppSpriteAtlas.forEachStitchTexture(FppParticleRegistry.particlePaths(),
						(namespace, path) -> registry.register(new ResourceLocation(namespace, path))));
		*///? }
	}
}
//? }
