package com.rimo.footprintparticle;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.particle.FootprintParticle;
import com.rimo.footprintparticle.particle.SnowDustParticle;
import com.rimo.footprintparticle.particle.WaterSplashParticle;
import com.rimo.footprintparticle.particle.WatermarkParticle;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeParticleRegistry {
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void register(ParticleFactoryRegisterEvent event) {
		MinecraftClient.getInstance().particleManager.registerFactory(FPPClient.FOOTPRINT.get(), FootprintParticle.DefaultFactory::new);
		MinecraftClient.getInstance().particleManager.registerFactory(FPPClient.WATERMARK.get(), WatermarkParticle.DefaultFactory::new);
		MinecraftClient.getInstance().particleManager.registerFactory(FPPClient.SNOWDUST.get(), SnowDustParticle.DefaultFactory::new);
		MinecraftClient.getInstance().particleManager.registerFactory(FPPClient.WATERSPLASH.get(), WaterSplashParticle.DefaultFactory::new);
	}
}
