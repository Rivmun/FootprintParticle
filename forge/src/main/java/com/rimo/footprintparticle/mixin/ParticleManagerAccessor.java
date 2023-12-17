package com.rimo.footprintparticle.mixin;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
	@Mixin(targets = "net/minecraft/client/particle/ParticleManager$SimpleSpriteProvider")
	interface SimpleSpriteProviderAccessor {
		@Accessor("sprites")
		List<Sprite> getSprites();
	}
}
