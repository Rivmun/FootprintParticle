package rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import rimo.footprintparticle.FPPClient;

public class WatermarkParticle extends FootprintParticle {

	protected WatermarkParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider) {
		super(clientWorld, x, y, z, vx, vy, vz, spriteProvider);
		this.setAlpha(0.5f * (FPPClient.CONFIG.getWetDuration() * 20 - (float) vy) / FPPClient.CONFIG.getWetDuration() / 20);
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			WatermarkParticle particle = new WatermarkParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
			if (parameters instanceof WatermarkParticleType printParameters)
				particle.checkEntitySize(printParameters, particle);
			return particle;
		}
	}
}
