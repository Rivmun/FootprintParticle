package com.rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SnowDustParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;

	// Copy from net.minecraft.client.particle.CloudParticle
	protected SnowDustParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider) {
		super(clientWorld, x, y, z, 0.0, 0.0, 0.0);
		this.velocityMultiplier = 0.96F;
		this.spriteProvider = spriteProvider;
		this.velocityX *= 0.10000000149011612;
		this.velocityY *= 0.10000000149011612;
		this.velocityZ *= 0.10000000149011612;
		this.velocityX += velocityX;
		//this.velocityY += velocityY;
		this.velocityZ += velocityZ;
		float g = 1.0F - (float)(Math.random() * 0.30000001192092896);
		this.red = g;
		this.green = g;
		this.blue = g;
		this.scale *= 1.875F;
		int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
		this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
		//this.collidesWithWorld = false;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.dead)
			this.setSpriteForAge(this.spriteProvider);
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			Particle particle = new SnowDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
			if (parameters instanceof SnowDustParticleType snowdust)
				particle.scale(snowdust.size);
			return particle;
		}
	}
}
