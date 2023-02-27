package rimo.footprintparticle;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rimo.footprintparticle.config.FPPConfig;

public class FPPClient implements ClientModInitializer {
	public static final String MODID = "footprintparticle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final ConfigHolder<FPPConfig> CONFIGHOLDER = AutoConfig.register(FPPConfig.class, GsonConfigSerializer::new);
	public static final FPPConfig CONFIG = CONFIGHOLDER.getConfig();

	public static FootprintParticleType FOOTPRINT;

	@Override
	public void onInitializeClient() {
		FOOTPRINT = Registry.register(Registry.PARTICLE_TYPE, MODID + ":footprint", new FootprintParticleType(true));
        ParticleFactoryRegistry.getInstance().register(FPPClient.FOOTPRINT, FootprintParticle.DefaultFactory::new);
	}
}
