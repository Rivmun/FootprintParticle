package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.particle.FppParticleRegistry;
import net.minecraft.client.renderer.texture.TextureAtlas;
//? if >= 1.20.1
import net.minecraft.client.renderer.texture.SpriteLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Particle Atlas Reload Listener<br>
 * The particle atlas instance was moved from {@code net.minecraft.client.particle.ParticleEngine}
 *  to {@code net.minecraft.client.resources.model.sprite.AtlasManager}, we want to make a general listener
 *  for all version, the only way is injected into {@link TextureAtlas} itself.<br>
 * <br>
 * We should filter out other atlas i.e. blocks/items but only grab the particles.
 *  When its reload/upload function was completed, call FppParticleRegistry.reloadSpriteSets() to refresh our particle's sprites.<br>
 * <br>
 * Why not use reload event hook by platform loaders: the structure of {@code spriteAtlasReady} event hook
 *  between different version / platform is unstable, Especially the fabric doesn't have one until 26.x.<br>
 * Current implementation is more generality and easily.
 */
@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
	@SuppressWarnings("deprecation")  // LOCATION_PARTICLES was marked deprecated since 1.21.11, but atlas still created with it.
	//? if < 1.20.1 {
	/*@Inject(method = "reload", at = @At("RETURN"))
	private void fpp$onParticlesAtlasReload(TextureAtlas.Preparations preparations, CallbackInfo ci) {
	*///? } else {
	@Inject(method = "upload", at = @At("RETURN"))
	private void fpp$onParticlesAtlasUpload(SpriteLoader.Preparations preparations, CallbackInfo ci) {
	//? }
		if (! TextureAtlas.LOCATION_PARTICLES.equals(((TextureAtlas) (Object) this).location()))
			return;
		FppParticleRegistry.reloadSpriteSets();
	}
}
