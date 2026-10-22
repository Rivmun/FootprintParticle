package com.rimo.footprintparticle.particle;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if > 1.18.2 {
import net.minecraft.util.RandomSource;
//? } else {
/*import java.util.Random;
*///? }

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	//? if <= 1.19.2
	//private static final String prefix = "particle/";
	private final String particlePath;
	/**
	 * Replaced as a whole by {@link #rebind(List)}; preserves atlas order for random/index lookups.
	 */
	private List<TextureAtlasSprite> sprites = Collections.emptyList();
	/**
	 * Name -> sprite lookup; custom texture names are file paths, so a name maps to one sprite.
	 */
	private final Map<String, TextureAtlasSprite> nameSpriteMap = new HashMap<>();

	public FppSpriteSet(String particlePath) {
		this.particlePath = particlePath;
	}

	String particlePath() {
		return this.particlePath;
	}

	/**
	 * Auto call {@link FppSpriteAtlas#reload(FppSpriteSet)} if sprites are empty.
	 * @return all sprites of this particle, in atlas order.
	 */
	public List<TextureAtlasSprite> sprites() {
		this.ensureLoaded();
		return this.sprites;
	}

	/**
	 * Look up custom sprites by their file names (no extension). Unknown names are skipped;
	 *  the result preserves the order of {@code names}.
	 */
	public List<TextureAtlasSprite> getSprites(String[] names) {
		this.ensureLoaded();
		if (names == null || names.length == 0)
			return Collections.emptyList();
		List<TextureAtlasSprite> result = new ArrayList<>(names.length);
		for (String name : names) {
			TextureAtlasSprite sprite = this.nameSpriteMap.get(name);
			if (sprite != null && ! result.contains(sprite))
				result.add(sprite);
		}
		return result;
	}

	private void ensureLoaded() {
		if (this.sprites.isEmpty())
			FppSpriteAtlas.reload(this);
	}

	public void rebind(List<TextureAtlasSprite> sprites) {
		this.sprites = sprites;
		this.nameSpriteMap.clear();
		for (TextureAtlasSprite sprite : sprites) {
			//? if <= 1.19.2 {
			/*String path = sprite.getName().getPath();
			String name = path.startsWith(prefix) ? path.substring(prefix.length()) : path;
			*///? } else {
			String name = sprite.contents().name().getPath();
			//? }
			this.nameSpriteMap.put(name, sprite);
		}
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
