//? if neoforge {
/*package com.rimo.footprintparticle.loaders.neoforge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.config.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.nio.file.Path;

/**
 * NeoForge client entry point; @Mod(value = ..., dist = Dist.CLIENT) keeps it off the dedicated server.
 * Only platform capability injection and the config screen extension are wired here.<br>
 *
 * Neoforge doesn't have <=1.19.2 version that no need to manual stitching.
 *
 * @see com.rimo.footprintparticle.particle.FppParticleRegistry
 * @see com.rimo.footprintparticle.particle.FppSpriteAtlas
 ^/
@Mod(value = Client.MOD_ID, dist = Dist.CLIENT)
public class Platform {
	static {
		PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
			public boolean isModLoaded(String id) {
				return ModList.get().isLoaded(id);
			}

			public Path getConfigFolder() {
				return FMLPaths.CONFIGDIR.get();
			}
		};
	}

	public Platform() {
	}

	@EventBusSubscriber(modid = Client.MOD_ID, value = Dist.CLIENT)
	public static class ClientEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			Client.init();

			if (ModList.get().isLoaded("cloth_config")) {
				ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
						container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) ->
								new ConfigScreen().build(parent)));
			}
		}
	}
}
*///? }
