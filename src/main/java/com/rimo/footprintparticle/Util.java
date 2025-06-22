package com.rimo.footprintparticle;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
//import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
//import virtuoel.pehkui.api.ScaleTypes;

import java.util.Arrays;
import java.util.List;

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

		if (entity.isBaby())
			scale *= 0.5f;
//		if (FabricLoader.getInstance().isModLoaded("pehkui"))
//			scale *= ScaleTypes.BASE.getScaleData(entity).getScale();

		scale *= entity.getScale();

		return scale;
	}

	public static List<Sprite> getCustomSprites(LivingEntity entity, SpriteProvider spriteProvider, String def) {
		String[] spriteNames = {def};
		for (String str : FPPClient.CONFIG.getCustomPrint()) {
			String[] str2 = str.split(",");
			try {
				if (str2[0].contentEquals(EntityType.getId(entity.getType()).toString())) {
					spriteNames = Arrays.copyOfRange(str2, 1, str2.length);
					break;
				}
			} catch (Exception e) {
				//
			}
		}
		List<String> finalSpriteNames = Arrays.asList(spriteNames);
		return ((FabricSpriteProvider) spriteProvider).getSprites().stream().filter(sprite ->
				finalSpriteNames.stream().anyMatch(str ->
						sprite.getContents().getId().getPath().contentEquals(str)
				)
		).toList();
	}
}
