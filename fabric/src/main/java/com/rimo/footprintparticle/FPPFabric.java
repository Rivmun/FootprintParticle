package com.rimo.footprintparticle;

import com.rimo.footprintparticle.particle.FootprintParticle;
import com.rimo.footprintparticle.particle.SnowDustParticle;
import com.rimo.footprintparticle.particle.WaterSplashParticle;
import com.rimo.footprintparticle.particle.WatermarkParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class FPPFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FPPClient.onInitializeClient();
		ParticleFactoryRegistry.getInstance().register(FPPClient.FOOTPRINT.get(), FootprintParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(FPPClient.WATERMARK.get(), WatermarkParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(FPPClient.SNOWDUST.get(), SnowDustParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(FPPClient.WATERSPLASH.get(), WaterSplashParticle.DefaultFactory::new);
	}
}
