package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class WatermarkParticle extends FootprintParticle {

	protected WatermarkParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider, FootprintParticleType parameters, String defName) {
		super(clientWorld, x, y, z, vx, vy, vz, spriteProvider, parameters, defName);
		this.setAlpha(FPPClient.CONFIG.getWatermarkAlpha() * (FPPClient.CONFIG.getWetDuration() * 20 - (float) vy) / FPPClient.CONFIG.getWetDuration() / 20);
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new WatermarkParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, (WatermarkParticleType) parameters, "watermark");
		}
	}
}
