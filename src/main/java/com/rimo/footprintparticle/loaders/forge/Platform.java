//? if forge {
/*package com.rimo.footprintparticle.loaders.forge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.config.ConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
//? if ! 1.16.5 {
//~ if < 1.19.2 '.ConfigScreenHandler' -> '.ConfigGuiHandler'
import net.minecraftforge.client.ConfigScreenHandler;
//? } else {
/^import net.minecraftforge.fml.ExtensionPoint;
^///? }

import java.nio.file.Path;

/**
 * Forge (1.16.5-1.20.1) client entry point; @Mod instantiates it through META-INF/mods.toml.
 * Only Forge-specific wiring: @Mod has no dist, so client-only setup runs from a Dist.CLIENT event subscriber,
 * and the config screen is registered through Forge's extension point.
 *
 * @see com.rimo.footprintparticle.particle.FppParticleRegistry
 * @see com.rimo.footprintparticle.particle.FppSpriteAtlas
 ^/
@Mod(Client.MOD_ID)
public class Platform {
	public Platform() {
	}

	@Mod.EventBusSubscriber(modid = Client.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientEvents {
		static {
			PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
				public boolean isModLoaded(String id) {
					return ModList.get().isLoaded(id);
				}

				public Path getConfigFolder() {
					return FMLPaths.CONFIGDIR.get();
				}
			};

			/*
			 * 1.19.2 and older: TextureStitchEvent is fired on the MOD bus, not the game bus;
			 * register the stitcher here so its @SubscribeEvent methods are picked up.
			 *
			 * The stitcher adds the texture ids returned by FppParticleRegistry.particlePaths(), so the
			 * particles must already be registered before the stitch runs. But the FMLClientSetupEvent
			 * (via onClientSetup) fires too late on these versions. When the stitcher invoked by event,
			 * the registry would still be empty and nothing will be stitched. That causes our particle
			 * cannot load texture but only black-purple tile.
			 * We must put our particle register besides stitcher register, to ensure that particle was
			 * registered before stitching.
			 ^/
			//? if forge && <= 1.19.2 {
			/^Client.init();
			net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus()
					.register(ForgeParticleStitcher.class);
			^///? }
		}

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			// 1.20.1 and later: texture stitch is data-driven by vanilla itself that particle JSON will be automatic loaded.
			// We can safely register our particle at anytime before particle has been created.
			//? if > 1.19.2
			Client.init();

			//~ if 1.16.5 'cloth_config' -> 'cloth-config'
			if (ModList.get().isLoaded("cloth_config")) {
				ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
						container.registerExtensionPoint(
								//? if ! 1.16.5 {
								//~ if < 1.19.2 'ConfigScreenHandler.ConfigScreenFactory' -> 'ConfigGuiHandler.ConfigGuiFactory' {
								ConfigScreenHandler.ConfigScreenFactory.class,
								() -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) ->
										new ConfigScreen().build(parent))));
								//~}
								//?} else {
								/^ExtensionPoint.CONFIGGUIFACTORY,
								() -> (client, parent) ->
										new ConfigScreen().build(parent)));
								^///?}
			}
		}
	}
}
*///? }
