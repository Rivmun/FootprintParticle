package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.FPPConfig;
import com.rimo.footprintparticle.particle.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FPPClient implements ClientModInitializer {
	public static final String MOD_ID = "footprintparticle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigHolder<FPPConfig> CONFIGHOLDER = AutoConfig.register(FPPConfig.class, GsonConfigSerializer::new);
	public static final FPPConfig CONFIG = CONFIGHOLDER.getConfig();

	public static final FootprintParticleType FOOTPRINT = Registry.register(Registries.PARTICLE_TYPE, MOD_ID + ":footprint", new FootprintParticleType(true));
	public static final WatermarkParticleType WATERMARK = Registry.register(Registries.PARTICLE_TYPE, MOD_ID + ":watermark", new WatermarkParticleType(true));
	public static final SnowDustParticleType SNOWDUST = Registry.register(Registries.PARTICLE_TYPE, MOD_ID + ":snowdust", new SnowDustParticleType(true));
	public static final WaterSplashParticleType WATERSPLASH = Registry.register(Registries.PARTICLE_TYPE, MOD_ID + ":watersplash", new WaterSplashParticleType(true));

	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(FPPClient.FOOTPRINT, FootprintParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(FPPClient.WATERMARK, WatermarkParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(FPPClient.SNOWDUST, SnowDustParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(FPPClient.WATERSPLASH, WaterSplashParticle.DefaultFactory::new);
	}
}
