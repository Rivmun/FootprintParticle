package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.Client;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Self-hosted Particle Registry<br>
 * <br>
 * Shared by all loaders, maps each of our {@link ParticleType} instances to its own {@link ParticleProvider}.<br>
 * <br>
 * These types are not registered into the vanilla {@code minecraft:particle_type} registry, because that registry
 *  is synced to whoever connects: clients without this mod would be kicked for an unknown particle type. Instead,
 *  {@link com.rimo.footprintparticle.mixin.ParticleEngineMixin} looks this table up at the HEAD of
 *  {@code ParticleEngine.makeParticle} and creates the particle with our provider on a hit.<br>
 * <br>
 * Providers and sprite sets are built once during class initialization, so loader/init order does not matter:
 *  the first lookup from the mixin or stitcher will trigger the static registration.<br>
 */
public final class FppParticleRegistry {
	private static final Map<ParticleType<?>, ParticleProvider<?>> PROVIDERS = new IdentityHashMap<>();
	private static final List<FppSpriteSet> SPRITE_SETS = new ArrayList<>();

	static {
		register(Client.FOOTPRINT, Client.FOOTPRINT_PATH, FootprintParticle.DefaultFactory::new);
		register(Client.WATERMARK, Client.WATERMARK_PATH, WatermarkParticle.DefaultFactory::new);
		register(Client.SNOWDUST, Client.SNOWDUST_PATH, SnowDustParticle.DefaultFactory::new);
		register(Client.WATERSPLASH, Client.WATERSPLASH_PATH, WaterSplashParticle.DefaultFactory::new);
	}

	private FppParticleRegistry() {
	}

	public static void register(ParticleType<?> type, String path, Function<FppSpriteSet, ? extends ParticleProvider<?>> factory) {
		FppSpriteSet spriteSet = new FppSpriteSet(path);
		SPRITE_SETS.add(spriteSet);
		PROVIDERS.put(type, factory.apply(spriteSet));
	}

	public static ParticleProvider<?> providerOf(ParticleType<?> type) {
		return PROVIDERS.get(type);
	}

	/**
	 * Invoked by resource reload listener ({@code TextureAtlasMixin})
	 * to reload all particle sprites.<br>
	 * The sprites cached per entity id still point at the old atlas, so they are dropped as well.
	 */
	public static void reloadSpriteSets() {
		for (FppSpriteSet spriteSet : SPRITE_SETS)
			FppSpriteAtlas.reload(spriteSet);
		FootprintParticle.clearCache();
		WatermarkParticle.clearCache();
	}

	//? if <= 1.19.2 {
	/*public static List<String> particlePaths() {
		List<String> paths = new ArrayList<>(SPRITE_SETS.size());
		for (FppSpriteSet spriteSet : SPRITE_SETS)
			paths.add(spriteSet.particlePath());
		return paths;
	}
	*///? }
}
