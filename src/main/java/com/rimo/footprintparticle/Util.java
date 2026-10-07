package com.rimo.footprintparticle;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//~ if < 1.21.11 '.Identifier' -> '.ResourceLocation'
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
//? if < 1.21.1
//import net.minecraft.world.entity.Mob;

import java.util.Arrays;
import java.util.List;

public class Util {
	public static float getEntityScale(LivingEntity entity) {
		float scale = 1f;

		Float configured = Client.SIZE_PER_MOB.get(EntityType.getKey(entity.getType()).toString());
		if (configured != null)
			scale *= configured;

		//? if < 1.21.11 {
		/*if (PlatformUtil.PLATFORM.isModLoaded("pehkui")) {
			try {
				scale *= virtuoel.pehkui.api.ScaleTypes.BASE.getScaleData(entity).getScale();
			} catch (Exception ignored) {}
		}
		*///? }
		// seems randommobsizes do not have its own scaling since vanilla makes one on 1.21+
		//? if < 1.21.1 && > 1.16.5 {
		/*if (PlatformUtil.PLATFORM.isModLoaded("random_mob_sizes") && entity instanceof Mob) {
			try {
				//? if ! 1.19.2 {
				scale *= ((com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon) entity).getMobScaling$RandomMobSizes();
				//? } else {
				/^scale *= ((com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon) entity).getMobScaling();  // randommobsizes version < 2.0
				^///? }
			} catch (Exception ignored) {}
		}
		*///? }

		if (entity.isBaby())
			scale *= 0.66f;
		scale *= entity.getScale();
		return scale;
	}

	public static List<TextureAtlasSprite> getCustomSprites(LivingEntity entity, SpriteSet spriteProvider, String def) {
		String[] spriteNames = Client.CUSTOM_PRINT.getOrDefault(
				EntityType.getKey(entity.getType()).toString(), new String[]{def});
		List<String> finalSpriteNames = Arrays.asList(spriteNames);
		List<TextureAtlasSprite> sprites = PlatformUtil.PLATFORM.getSprites(spriteProvider);
		List<TextureAtlasSprite> result = sprites.stream().filter(sprite ->
				finalSpriteNames.stream().anyMatch(str ->
						//? if <= 1.19.2 {
						/*sprite.getName().getPath().substring(9).contentEquals(str)  // remove 'particle/'
						*///? } else {
						sprite.contents().name().getPath().contentEquals(str)
						//? }
				)
		).toList();
		if (result.isEmpty()) {
			Client.LOGGER.error("Wrong custom texture for {}, please check.", EntityType.getKey(entity.getType()));
			return sprites;
		}
		return result;
	}

	//? if > 1.21.1 {
	public static Identifier getId(String path) {
		return Identifier.fromNamespaceAndPath(Client.MOD_ID, path);
	}
	//? } else {
	/*public static ResourceLocation getId(String path) {
		//? if <= 1.20.1 {
		/^return new ResourceLocation(Client.MOD_ID, path);
		 ^///? } else {
		return ResourceLocation.fromNamespaceAndPath(Client.MOD_ID, path);
		//? }
	}
	*///? }
}
