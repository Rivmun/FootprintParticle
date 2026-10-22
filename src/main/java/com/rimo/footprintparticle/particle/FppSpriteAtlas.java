//~ if < 1.21.11 'Identifier' -> 'ResourceLocation' {
package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.VersionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.Identifier;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//? if <= 1.19.2
//import java.util.function.BiConsumer;

/**
 * Particle Atlas Access for Self-hosted Particles<br>
 * <br>
 * Reads {@code particles/<path>.json} for texture ids and resolves them against the vanilla particle atlas
 *  on demand. This class keeps no cache: the cache and its invalidation live in {@link FppSpriteSet}.<br>
 * <br>
 * The atlas is resource-driven on 1.20.1+; on 1.19.2 and older each loader must add our textures through
 *  {@code forEachStitchTexture} before stitching.<br>
 */
public final class FppSpriteAtlas {
	private FppSpriteAtlas() {
	}

	/**
	 * Get a single sprite from the particle atlas by id **instantly**. (<=1.19.2 must contain 'particle/' prefix.)
	 * if particle atlas not be ready, or in stitch progress, it's return null.
	 */
	@SuppressWarnings("deprecation")
	public static TextureAtlasSprite sprite(Identifier id) {
		// DO NOT use Minecraft.getTextureAtlas, it's return ModelManager.AtlasSet, no particle atlas.
		// The particle atlas is always registered in TextureManager, so query it directly.
		AbstractTexture texture = Minecraft.getInstance().getTextureManager()
				.getTexture(TextureAtlas.LOCATION_PARTICLES);
		if (! (texture instanceof TextureAtlas))
			return null;
		try {
			return ((TextureAtlas) texture).getSprite(id);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	/**
	 * Get sprites from the given texture list. Maybe empty.
	 */
	public static List<TextureAtlasSprite> spritesOf(List<Identifier> textures) {
		List<TextureAtlasSprite> sprites = new ArrayList<>(textures.size());
		for (Identifier texture : textures) {
			TextureAtlasSprite sprite = sprite(texture);
			if (sprite != null)
				sprites.add(sprite);
		}
		return sprites;
	}

	/**
	 * Re-read the particle's texture list and rebind all sprites from the current atlas.
	 * Invoked in batch by {@link FppParticleRegistry#reloadSpriteSets()} after the particle atlas is re-stitched.
	 */
	public static void reload(FppSpriteSet spriteSet) {
		List<Identifier> textures = readTextures(spriteSet.particlePath());
		List<TextureAtlasSprite> sprites = spritesOf(textures);
		if (sprites.isEmpty()) {
			TextureAtlasSprite missing = sprite(MissingTextureAtlasSprite.getLocation());
			if (missing == null)
				return; // atlas not ready: never push a null sprite, keep the current cache until next reload.
			sprites = Collections.singletonList(missing);
		}
		spriteSet.rebind(sprites);
	}

	//? if <= 1.19.2 {
	/*/^^
	 ^ Add each texture of each particle to vanilla particle atlas when stitching.
	 ^ Invoked by forge / fabric platform event hook.
	 ^/
	public static void forEachStitchTexture(Iterable<String> particlePaths, BiConsumer<String, String> adder) {
		for (String particlePath : particlePaths)
			for (Identifier texture : readTextures(particlePath))
				adder.accept(texture.getNamespace(), texture.getPath());
	}
	*///? }

	/**
	 * Read textureDescription JSON file instantly. Invoked when reload or stitching.
	 * Return a texture id list to get sprite from engine, may be empty.
	 */
	public static List<Identifier> readTextures(String particlePath) {
		List<Identifier> out = new ArrayList<>();
		try {
			Identifier id = VersionUtil.getId(Client.MOD_ID, "particles/" + particlePath + ".json");
			//? if < 1.19.2 {
			/*InputStream stream = Minecraft.getInstance().getResourceManager()
					.getResource(id)
					.getInputStream();
			*///? } else {
			InputStream stream = Minecraft.getInstance().getResourceManager()
					.getResourceOrThrow(id)
					.open();
			//? }
			try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
				// Use vanilla descriptor here instead of make our own. That's cross-version stable and easy.
				List<Identifier> textures = ParticleDescription.fromJson(GsonHelper.parse(reader)).getTextures();
				if (textures == null || textures.isEmpty()) {  // 1.16.5 can be null, leave it.
					Client.LOGGER.warn("Particle description for '{}' has no \"textures\"; its textures stay "
							+ "unavailable until the file can be read again.", particlePath);
					return out;
				}
				for (Identifier texture : textures)
					out.add(toAtlasTexture(texture));
			}
		} catch (FileNotFoundException e) {
			Client.LOGGER.warn("No particle description found for '{}'; its textures stay unavailable "
					+ "until the file can be read again.", particlePath);
		} catch (Exception e) {
			Client.LOGGER.warn("Failed to read particle description for '{}'; its textures stay unavailable "
					+ "until the file can be read again.", particlePath, e);
		}
		return out;
	}

	/**
	 * fix texture path, especially for version <= 1.19.2.
	 */
	private static Identifier toAtlasTexture(Identifier texture) {
		//? if <= 1.19.2 {
		/*String path = texture.getPath();
		if (path.startsWith("particle/"))
			return texture;
		return VersionUtil.getId(texture.getNamespace(), "particle/" + path);
		*///? } else {
		return texture;
		//? }
	}
}
//~ }
