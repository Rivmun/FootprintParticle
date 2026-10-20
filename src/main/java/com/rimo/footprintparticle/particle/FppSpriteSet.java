package com.rimo.footprintparticle.particle;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if > 1.18.2 {
import net.minecraft.util.RandomSource;
//? } else {
/*import java.util.Random;
*///? }

import java.util.Collections;
import java.util.List;

/**
 * Sprite Set of a Self-hosted Particle<br>
 * {@link FppSpriteAtlas} resolves the sprite list on reload and pushes it in via {@link #rebind(List)};
 *  invalidation runs in batch from {@link FppParticleRegistry#reloadSpriteSets()}.<br>
 * <br>
 * There is no vanilla {@code MutableSpriteSet} to borrow, so sprite lookup and rebinding are implemented here.<br>
 *
 * @see FppParticleRegistry
 * @see FppSpriteAtlas
 */
public class FppSpriteSet implements SpriteSet {
	private final String particlePath;
	/**
	 * Only replaced as a whole by {@link #rebind(List)}; getters read it directly and never probe the atlas.
	 */
	private List<TextureAtlasSprite> sprites = Collections.emptyList();

	public FppSpriteSet(String particlePath) {
		this.particlePath = particlePath;
	}

	String particlePath() {
		return this.particlePath;
	}

	/**
	 * Auto call {@link FppSpriteAtlas#reload(FppSpriteSet)} if sprites are empty.
	 * @return all sprites of this particle.
	 */
	public List<TextureAtlasSprite> sprites() {
		if (this.sprites.isEmpty())
			FppSpriteAtlas.reload(this);
		return this.sprites;
	}

	public void rebind(List<TextureAtlasSprite> sprites) {
		this.sprites = sprites;
	}

	/*
	 * Below are implementations of SpriteSet.
	 * just copy from vanilla, see
	 * in net.minecraft.client.particle.ParticleEngine$MutableSpriteSet.
	 * or net.minecraft.client.particle.ParticleResources$MutableSpriteSet.
	 *
	 * Must use this.sprites() so the cache can lazily fill itself before the first reload callback.
	 */

	public TextureAtlasSprite get(int index, int max) {
		List<TextureAtlasSprite> sprites = this.sprites();
		return sprites.get(index * (sprites.size() - 1) / max);
	}

	//~ if > 1.18.2 'Random' -> 'RandomSource'
	public TextureAtlasSprite get(RandomSource random) {
		List<TextureAtlasSprite> sprites = this.sprites();
		return sprites.get(random.nextInt(sprites.size()));
	}

	//? if >= 1.21.11 {
	public TextureAtlasSprite first() {
		return this.sprites().getFirst();
	}
	//? }
}
