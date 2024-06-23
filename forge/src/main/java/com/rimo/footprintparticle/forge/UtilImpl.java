package com.rimo.footprintparticle.forge;

import com.rimo.footprintparticle.mixin.ParticleManagerAccessor;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;

import java.util.List;
import java.util.stream.Collectors;

public class UtilImpl {
	public static List<Sprite> getSpriteListExpectPlatform(SpriteProvider spriteProvider, List<String> finalSpriteNames) {
		return ((ParticleManagerAccessor.SimpleSpriteProviderAccessor) spriteProvider).getSprites().stream().filter(sprite ->
				finalSpriteNames.stream().anyMatch(str ->
						sprite.getId().getPath().substring(9).contentEquals(str)
				)
		).collect(Collectors.toList());
	}
}
