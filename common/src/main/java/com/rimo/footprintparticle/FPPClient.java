package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.FPPConfig;
import com.rimo.footprintparticle.particle.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FPPClient {
	public static final String MOD_ID = "footprintparticle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigHolder<FPPConfig> CONFIGHOLDER = AutoConfig.register(FPPConfig.class, GsonConfigSerializer::new);
	public static final FPPConfig CONFIG = CONFIGHOLDER.getConfig();

	public static final FootprintParticleType FOOTPRINT = Registry.register(Registries.PARTICLE_TYPE, FPPClient.MOD_ID + ":footprint", new FootprintParticleType(true));
	public static final WatermarkParticleType WATERMARK = Registry.register(Registries.PARTICLE_TYPE, FPPClient.MOD_ID + ":watermark", new WatermarkParticleType(true));
	public static final SnowDustParticleType SNOWDUST = Registry.register(Registries.PARTICLE_TYPE, FPPClient.MOD_ID + ":snowdust", new SnowDustParticleType(true));
	public static final WaterSplashParticleType WATERSPLASH = Registry.register(Registries.PARTICLE_TYPE, FPPClient.MOD_ID + ":watersplash", new WaterSplashParticleType(true));

	public static void onInitializeClient() {
		ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
		registry.register(FOOTPRINT, FootprintParticle.DefaultFactory::new);
		registry.register(WATERMARK, WatermarkParticle.DefaultFactory::new);
		registry.register(SNOWDUST, SnowDustParticle.DefaultFactory::new);
		registry.register(WATERSPLASH, WaterSplashParticle.DefaultFactory::new);
	}
}
