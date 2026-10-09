package com.rimo.footprintparticle.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;
//? if > 1.21.1 {
import net.minecraft.util.RandomSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? }
import org.jspecify.annotations.NonNull;

public class WaterSplashParticle extends WaterDropParticle {

	// Vanilla's splash & rain particle can't apply vy (h), so we made custom one to override it.
	//~ if < 1.21.11 'double i, TextureAtlasSprite sprite' -> 'double i'
	protected WaterSplashParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, TextureAtlasSprite sprite) {
		//~ if < 1.21.11 'f, sprite' -> 'f'
		super(clientWorld, d, e, f, sprite);
		this.gravity = 0.04F;
		this.xd = g;
		this.yd = 0.1f + h;
		this.zd = i;
		this.lifetime *= 10;
	}

	public static class DefaultFactory implements ParticleProvider<@NonNull SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		//? if < 1.21.11 {
		/*public Particle createParticle(@NonNull SimpleParticleType defaultParticleType, @NonNull ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			WaterSplashParticle waterSplashParticle = new WaterSplashParticle(clientWorld, d, e, f, g, h, i);
			waterSplashParticle.pickSprite(this.spriteProvider);
			return waterSplashParticle;
		}
		*///? } else {
		public Particle createParticle(@NonNull SimpleParticleType defaultParticleType, @NonNull ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, @NonNull RandomSource random) {
			return new WaterSplashParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.get(random));
		}
		//? }
	}
}
