package com.rimo.footprintparticle;

import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.ParticleProviderRegistry;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
	public static float getEntityScale(LivingEntity entity) {
		float scale = 1f;

		for (String str : FPPClient.CONFIG.getSizePerMob()) {
			String[] str2 = str.split(",");
			try {
				if (str2[0].contentEquals(EntityType.getId(entity.getType()).toString())) {
					scale *= Float.parseFloat(str2[1]);
				}
			} catch (Exception e) {
				// Ignore...
			}
		}

		if (Platform.isModLoaded("pehkui"))
			scale *= ScaleTypes.BASE.getScaleData(entity).getScale();

		if (entity.isBaby())
			scale *= 0.66f;

		return scale;
	}

	public static List<Sprite> getCustomSprites(LivingEntity entity, SpriteProvider spriteProvider, String def) {
		String[] spriteNames = {def};
		for (String str : FPPClient.CONFIG.getCustomPrint()) {
			String[] str2 = str.split(",");
			try {
				if (str2[0].contentEquals(EntityType.getId(entity.getType()).toString())) {
					spriteNames = str2;
					break;
				}
			} catch (Exception e) {
				//
			}
		}
		List<String> finalSpriteNames = Arrays.asList(spriteNames);
		return ((ParticleProviderRegistry.ExtendedSpriteSet) spriteProvider).getSprites().stream().filter(sprite ->
				finalSpriteNames.stream().anyMatch(str ->
						sprite.getId().getPath().substring(9).contentEquals(str)
				)
		).collect(Collectors.toList());
	}
}
