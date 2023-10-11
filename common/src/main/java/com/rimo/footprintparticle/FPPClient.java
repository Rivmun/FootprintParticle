package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.FPPConfig;
import com.rimo.footprintparticle.particle.*;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.RegistryKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import virtuoel.pehkui.api.ScaleTypes;

public class FPPClient {
	public static final String MOD_ID = "footprintparticle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigHolder<FPPConfig> CONFIGHOLDER = AutoConfig.register(FPPConfig.class, GsonConfigSerializer::new);
	public static final FPPConfig CONFIG = CONFIGHOLDER.getConfig();

	public static final DeferredRegister<ParticleType<?>> PARTICLE = DeferredRegister.create(MOD_ID, RegistryKeys.PARTICLE_TYPE);

	public static final RegistrySupplier<FootprintParticleType> FOOTPRINT = PARTICLE.register("footprint", () -> new FootprintParticleType(false));
	public static final RegistrySupplier<WatermarkParticleType> WATERMARK = PARTICLE.register("watermark", () -> new WatermarkParticleType(false));
	public static final RegistrySupplier<SnowDustParticleType> SNOWDUST = PARTICLE.register("snowdust", () -> new SnowDustParticleType(false));
	public static final RegistrySupplier<WaterSplashParticleType> WATERSPLASH = PARTICLE.register("watersplash", () -> new WaterSplashParticleType(false));

	public static void onInitializeClient() {
        PARTICLE.register();
		if (Platform.getEnvironment() == Env.CLIENT) {
			ParticleProviderRegistry.register(FOOTPRINT, FootprintParticle.DefaultFactory::new);
			ParticleProviderRegistry.register(WATERMARK, WatermarkParticle.DefaultFactory::new);
			ParticleProviderRegistry.register(SNOWDUST, SnowDustParticle.DefaultFactory::new);
			ParticleProviderRegistry.register(WATERSPLASH, WaterSplashParticle.DefaultFactory::new);
		}
	}

	public static float getEntityScale(LivingEntity entity) {
		float scale = 1f;

		try {
			for (String str : FPPClient.CONFIG.getSizePerMob()) {
				String[] str2 = str.split(",");
				if (str2[0].contentEquals(EntityType.getId(entity.getType()).toString())) {
					scale *= Float.parseFloat(str2[1]);
				}
			}
		} catch (Exception e) {
			// Ignore...
		}

		if (Platform.isModLoaded("pehkui"))
			scale *= ScaleTypes.BASE.getScaleData(entity).getScale();

		if (entity.isBaby())
			scale *= 0.66f;

		return scale;
	}
}
