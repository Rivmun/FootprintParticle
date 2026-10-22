package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.particle.*;
//? if = 1.16.5 {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///? } else {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? }

/**
 * Shared Entry Point of the Client-only Mod<br>
 * Holds MOD_ID, the logger, the config singleton and the four particle type instances.<br>
 *
 * @see FppParticleRegistry
 * @see com.rimo.footprintparticle.mixin.ParticleEngineMixin
 */
public class Client {
	public static final String MOD_ID = "footprintparticle";
	//~if = 1.16.5 'LoggerFactory.' -> 'LogManager.'
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Config CONFIG = Config.load();

	// Our Particles...
	public static final FootprintParticleType FOOTPRINT = new FootprintParticleType(true);
	public static final WatermarkParticleType WATERMARK = new WatermarkParticleType(true);
	public static final SnowDustParticleType SNOWDUST = new SnowDustParticleType(true);
	public static final WaterSplashParticleType WATERSPLASH = new WaterSplashParticleType(true);

	public static void init() {
		/*
		 * The registration is moved to FppParticleRegistry static block,
		 * to ensure register runs before stitch and first reload, prevent registry order issues, especially on forge.
		 *
		 * @see com.rimo.footprintparticle.loaders.forge.Platform$ClientEvents.<clinit>()
		 */
	}
}
