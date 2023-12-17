package com.rimo.footprintparticle;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleTypes;

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
		/*
		 * Due to particle issue on ArchAPI, we can not cast spriteProvider to ArchImpl.
		 * On forge side, mixin to ParticleManager.SimpleSpriteProvider to access sprites is ok,
		 * but fabric side, only using fabric-api can access because spriteProvider actually a FabricSpriteProvider, not a vanilla itself.
		 * So we must cast spriteProvider on each side individually.
		 */
		return getSpriteListExpectPlatform(spriteProvider,finalSpriteNames);
	}

	@ExpectPlatform
	public static List<Sprite> getSpriteListExpectPlatform(SpriteProvider spriteProvider, List<String> finalSpriteNames) {
		throw new AssertionError();
	}
}
