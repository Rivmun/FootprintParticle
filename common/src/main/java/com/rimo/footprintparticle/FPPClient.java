package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.FPPConfig;
import com.rimo.footprintparticle.particle.*;
import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.registry.RegistrySupplier;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4J2LoggerFactory;

public class FPPClient {
	public static final String MOD_ID = "footprintparticle";
	public static final InternalLogger LOGGER = Log4J2LoggerFactory.getInstance(MOD_ID);

	public static final ConfigHolder<FPPConfig> CONFIGHOLDER = AutoConfig.register(FPPConfig.class, GsonConfigSerializer::new);
	public static final FPPConfig CONFIG = CONFIGHOLDER.getConfig();

	public static final DeferredRegister<ParticleType<?>> PARTICLE = DeferredRegister.create(MOD_ID, Registry.PARTICLE_TYPE_KEY);

	public static final RegistrySupplier<FootprintParticleType> FOOTPRINT = PARTICLE.register("footprint", () -> new FootprintParticleType(false));
	public static final RegistrySupplier<WatermarkParticleType> WATERMARK = PARTICLE.register("watermark", () -> new WatermarkParticleType(false));
	public static final RegistrySupplier<SnowDustParticleType> SNOWDUST = PARTICLE.register("snowdust", () -> new SnowDustParticleType(false));
	public static final RegistrySupplier<WaterSplashParticleType> WATERSPLASH = PARTICLE.register("watersplash", () -> new WaterSplashParticleType(false));

	public static void onInitializeClient() {
        PARTICLE.register();
		/*
		 * Architectury's particle registries has a critical issue in 1.16.5 and has not been resolved yet,
		 * so we can only register the particle per platform manually.
		 */
	}
}
