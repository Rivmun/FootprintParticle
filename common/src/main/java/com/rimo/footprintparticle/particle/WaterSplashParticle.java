package com.rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class WaterSplashParticle extends RainSplashParticle {

	// Vanilla's splash & rain particle can't apply vy (h), so we made custom one to override it.
	protected WaterSplashParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f);
		this.gravityStrength = 0.04F;
		this.velocityX = g;
		this.velocityY = 0.1f + h;
		this.velocityZ = i;
		this.maxAge *= 10;
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			WaterSplashParticle waterSplashParticle = new WaterSplashParticle(clientWorld, d, e, f, g, h, i);
			waterSplashParticle.setSprite(this.spriteProvider);
			return waterSplashParticle;
		}
	}
}
