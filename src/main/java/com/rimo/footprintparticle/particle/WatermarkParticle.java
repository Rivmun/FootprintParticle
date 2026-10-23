package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.Client;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
//? if > 1.21.1
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rimo.footprintparticle.Client.CONFIG;

public class WatermarkParticle extends FootprintParticle {
	private static final String[] DEF_TEXTURE = new String[]{Client.WATERMARK_PATH};
	/**
	 * EntityId -> resolved sprites of watermark its own. see {@code FootprintParticle#ID2SPRITE_MAP}.
	 */
	private static final Map<String, List<TextureAtlasSprite>> ID2SPRITE_MAP = new HashMap<>();

	protected WatermarkParticle(ClientLevel clientWorld, double x, double y, double z, double dx, double size, double dz, FppSpriteSet spriteProvider, WatermarkParticleType parameters) {
		super(clientWorld, x, y, z, dx, size, dz, spriteProvider, parameters);
		this.setAlpha(CONFIG.getWatermarkAlpha() * (CONFIG.getWetDuration() * 20 - (float) parameters.startAlpha) / CONFIG.getWetDuration() / 20);
		this.lifetime = (int) (CONFIG.getWatermarkLifetime() * 20);
	}

	@Override
	protected String[] getDefTexture() {
		return DEF_TEXTURE;
	}

	@Override
	protected Map<String, List<TextureAtlasSprite>> getCache() {
		return ID2SPRITE_MAP;
	}

	/**
	 * Override of {@link FootprintParticle#clearCache()} to clear watermark own {@link #ID2SPRITE_MAP}.
	 */
	public static void clearCache() {
		ID2SPRITE_MAP.clear();
	}

	public static class DefaultFactory implements ParticleProvider<@NonNull SimpleParticleType> {
		private final FppSpriteSet spriteProvider;

		public DefaultFactory(FppSpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		//~ if < 1.21.11 'double dz, @NonNull RandomSource random' -> 'double dz'
		public Particle createParticle(@NonNull SimpleParticleType parameters, @NonNull ClientLevel world, double x, double y, double z, double dx, double size, double dz, @NonNull RandomSource random) {
			return new WatermarkParticle(world, x, y, z, dx, size, dz, this.spriteProvider, (WatermarkParticleType) parameters);
		}
	}
}
