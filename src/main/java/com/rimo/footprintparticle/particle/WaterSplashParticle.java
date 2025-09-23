package com.rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

public class WaterSplashParticle extends RainSplashParticle {

	// Vanilla's splash & rain particle can't apply vy (h), so we made custom one to override it.
	protected WaterSplashParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
		super(clientWorld, d, e, f, sprite);
		this.gravityStrength = 0.04F;
		this.velocityX = g;
		this.velocityY = 0.1f + h;
		this.velocityZ = i;
		this.maxAge *= 10;
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
			return new WaterSplashParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
		}
	}
}
