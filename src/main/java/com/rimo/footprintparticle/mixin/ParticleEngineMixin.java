package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.particle.FppParticleRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 1.21.11 {
import net.minecraft.util.RandomSource;
//? }

/**
 * Particle Hijack for All Loaders<br>
 * <br>
 * Looks {@link FppParticleRegistry} up at the HEAD of {@code ParticleEngine.makeParticle}. On a hit, the particle
 *  is created with our own provider and the vanilla method is cancelled; otherwise the call is left untouched.<br>
 * <br>
 * We deliberately do not shadow vanilla {@code providers} / {@code spriteSets}: their concrete types differ
 *  between loaders ({@code Int2ObjectMap} on Fabric, {@code Map} on NeoForge) and 1.21.11+ moved them into
 *  {@code ParticleResources}, so one cross-loader shadow is impossible.<br>
 */
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	// makeParticle reads the level from here and passes it to the provider;
	// not final because setLevel reassigns it.
	@Shadow protected ClientLevel level;

	// Handler signature mirrors the target parameters one-for-one, plus CallbackInfoReturnable;
	// T only needs the erased ParticleOptions.
	@SuppressWarnings("unchecked")
	@Inject(method = "makeParticle", at = @At("HEAD"), cancellable = true)
	private <T extends ParticleOptions> void fpp$makeParticle(T type, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
		ParticleProvider<T> provider = (ParticleProvider<T>) FppParticleRegistry.providerOf(type.getType());
		if (provider == null)
			return;
		//~ if < 1.21.11 'zSpeed, RandomSource.create()' -> 'zSpeed'
		cir.setReturnValue(provider.createParticle(type, this.level, x, y, z, xSpeed, ySpeed, zSpeed, RandomSource.create()));
	}
}
