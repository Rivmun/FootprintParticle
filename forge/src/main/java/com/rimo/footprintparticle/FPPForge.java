package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.ConfigScreen;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod(FPPClient.MOD_ID)
public class FPPForge {
	public FPPForge() {
		// Submit our event bus to let architectury register our content on the right time
		EventBuses.registerModEventBus(FPPClient.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		FPPClient.onInitializeClient();

		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> FPPForge::registerModsPage);

		DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> FPPClient.LOGGER.warn("'FootprintParticle' is a client side mod, it's should be removed from server mod folder."));
	}

	public static void registerModsPage() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> new ConfigScreen().buildScreen());
	}
}
