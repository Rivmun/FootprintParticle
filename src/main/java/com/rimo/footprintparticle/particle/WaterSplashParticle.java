package com.rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public class WaterSplashParticle extends WaterDropParticle {

	// Vanilla's splash & rain particle can't apply vy (h), so we made custom one to override it.
	protected WaterSplashParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, TextureAtlasSprite sprite) {
		super(clientWorld, d, e, f, sprite);
		this.gravity = 0.04F;
		this.xd = g;
		this.yd = 0.1f + h;
		this.zd = i;
		this.lifetime *= 10;
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleProvider<@NotNull SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType defaultParticleType, @NotNull ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, @NotNull RandomSource random) {
			return new WaterSplashParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.get(random));
		}
	}
}
