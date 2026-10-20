//? if forge && <= 1.19.2 {
/*package com.rimo.footprintparticle.loaders.forge;

import com.rimo.footprintparticle.particle.FppSpriteAtlas;
import com.rimo.footprintparticle.particle.FppParticleRegistry;
import com.rimo.footprintparticle.VersionUtil;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Forge-only texture stitcher for 1.19.2 and older: adds our particle textures during TextureStitchEvent.Pre.
 *
 * @see FppSpriteAtlas
 * @see FppParticleRegistry
 ^/
public class ForgeParticleStitcher {
	//~ if 1.16.5 '.getAtlas()' -> '.getMap()' {
	@SubscribeEvent
	public static void onStitchPre(TextureStitchEvent.Pre event) {
		if (!event.getAtlas().location().equals(VersionUtil.getId("minecraft", "textures/atlas/particles.png")))
			return;
		FppSpriteAtlas.forEachStitchTexture(FppParticleRegistry.particlePaths(),
				(namespace, path) -> event.addSprite(VersionUtil.getId(namespace, path)));
	}
	//~ }
}
*///? }
