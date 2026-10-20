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
		 * Register to our own particle provider.
		 * Why? The official particle register of Fabric / Neoforge still use vanilla particleSheet.
		 * Though their register only run in client, but the client will be a server in LAN game.
		 * The particleSheet will sync to client which trying to connect the host, that's make a conflict and connect will be fail.
		 * Completely self-hold entire particle provider is the only way to fix it.
		 */
		FppParticleRegistry.register(Client.FOOTPRINT, "footprint", FootprintParticle.DefaultFactory::new);
		FppParticleRegistry.register(Client.WATERMARK, "watermark", WatermarkParticle.DefaultFactory::new);
		FppParticleRegistry.register(Client.SNOWDUST, "snowdust", SnowDustParticle.DefaultFactory::new);
		FppParticleRegistry.register(Client.WATERSPLASH, "watersplash", WaterSplashParticle.DefaultFactory::new);
	}
}
