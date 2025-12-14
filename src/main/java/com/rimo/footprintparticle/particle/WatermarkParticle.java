package com.rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import static com.rimo.footprintparticle.FPPClient.CONFIG;

public class WatermarkParticle extends FootprintParticle {

	protected WatermarkParticle(ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteProvider, FootprintParticleType parameters, String defName) {
		super(clientWorld, x, y, z, vx, vy, vz, spriteProvider, parameters, defName);
		this.setAlpha(CONFIG.getWatermarkAlpha() * (CONFIG.getWetDuration() * 20 - (float) vy) / CONFIG.getWetDuration() / 20);
		this.lifetime = (int) (CONFIG.getWatermarkLifetime() * 20);
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleProvider<@NotNull SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, @NotNull RandomSource random) {
			return new WatermarkParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, (WatermarkParticleType) parameters, "watermark");
		}
	}
}
