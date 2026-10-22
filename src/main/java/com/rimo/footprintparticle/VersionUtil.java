package com.rimo.footprintparticle;

//~ if < 1.19.3 '.registries.Registries' -> '.Registry'
import net.minecraft.core.registries.Registries;
//~ if < 1.21.11 '.Identifier' -> '.ResourceLocation'
import net.minecraft.resources.Identifier;
//? if > 1.18.2
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if 1.16.5 {
/*import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import java.util.Collections;
*///? } else {
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
//? }
//? if < 1.21.11
//import com.rimo.footprintparticle.PlatformUtil;
//? if < 1.21.1 && > 1.16.5
//import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Full of Stonecutter language here as you like...
 */
public final class VersionUtil {
	//~ if < 1.21.11 'Identifier' -> 'ResourceLocation'
	//~ if < 1.21.1 '.withDefaultNamespace' -> '.tryParse'
	//~ if < 1.19.3 'Registries.BLOCK' -> 'Registry.BLOCK_REGISTRY'
	//? if > 1.18.2
	private static final ResourceKey<Block> fpp$AIR = ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("air"));
	//? if < 1.21.11
	//private static boolean isNoPehkui;
	//? if < 1.21.1 && > 1.16.5
	//private static boolean isNoRandomMobSizes;

	//? if > 1.21.1 {
	public static Identifier getId(String path) {
		return Identifier.fromNamespaceAndPath(Client.MOD_ID, path);
	}

	public static Identifier getId(String namespace, String path) {
		return Identifier.fromNamespaceAndPath(namespace, path);
	}
	//? } else {
	/*public static ResourceLocation getId(String path) {
		return getId(Client.MOD_ID, path);
	}

	public static ResourceLocation getId(String namespace, String path) {
		//? if <= 1.20.1 {
		/^return new ResourceLocation(namespace, path);
		^///? } else {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
		//? }
	}
	*///? }

	public static String getBlockName(BlockState block) {
		//? if < 1.19.2 {
		/*return Registry.BLOCK.getKey(block.getBlock()).toString();
		*///? } else if < 1.21.11{
		/*return block.getBlockHolder().unwrapKey().orElse(fpp$AIR).location().toString();
		 *///? } else if < 26.1 {
		/*return block.getBlockHolder().unwrapKey().orElse(fpp$AIR).identifier().toString();
		 *///? } else {
		return block.typeHolder().unwrapKey().orElse(fpp$AIR).identifier().toString();
		 //? }
	}

	//? if 1.16.5 {
	/*private static Collection<ResourceLocation> getBlockTags(BlockState block) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection == null)
			return Collections.emptyList();
		return connection.getTags().getBlocks().getMatchingTags(block.getBlock());
	}
	*///? } else {
	private static List<TagKey<Block>> getBlockTags(BlockState block) {
		//~ if < 26.1 '.tags()' -> '.getTags()'
		return block.tags().toList();
	}
	//? }

	public static List<String> getBlockTagNames(BlockState block) {
		return getBlockTags(block).stream().map(tag ->
				//~ if 1.16.5 '.location().toString()' -> '.toString()'
				"#" + tag.location().toString()  // DO NOT remove '.toString()', IDE is a liar!
		).collect(Collectors.toList());
	}

	//? if 1.16.5 {
	/*private static Collection<ResourceLocation> getEntityTags(EntityType<?> type) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection == null)
			return Collections.emptyList();
		return connection.getTags().getEntityTypes().getMatchingTags(type);
	}
	*///? } else {
	private static List<TagKey<EntityType<?>>> getEntityTags(EntityType<?> type) {
		return type.builtInRegistryHolder().tags().collect(Collectors.toList());
	}
	//? }

	public static List<String> getEntityTagNames(EntityType<?> type) {
		return getEntityTags(type).stream().map(tag ->
				//~ if 1.16.5 '.location().toString()' -> '.toString()'
				"#" + tag.location().toString()
		).collect(Collectors.toList());
	}

	// Don't inline this function into mixin class that mixin descriptor will resolve every reference
	// even target class not be import just a full name invoke.
	// It will crash the game on boot.
	public static float getEntityScale(String id, List<String> tags, LivingEntity entity) {
		float scale = Client.CONFIG.getMobSize(id, tags);

		//? if < 1.21.11 {
		/*if (! isNoPehkui && PlatformUtil.PLATFORM.isModLoaded("pehkui")) {
			try {
				scale *= virtuoel.pehkui.api.ScaleTypes.BASE.getScaleData(entity).getScale();
			} catch (Exception | LinkageError e) {
				isNoPehkui = true;
			}
		}
		*///? }
		// seems randommobsizes do not have its own scaling since vanilla makes one on 1.21+
		//? if < 1.21.1 && > 1.16.5 {
		/*if (! isNoRandomMobSizes && PlatformUtil.PLATFORM.isModLoaded("random_mob_sizes") && entity instanceof Mob) {
			try {
				//? if ! 1.19.2 {
				scale *= ((com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon) entity).getMobScaling$RandomMobSizes();
				//? } else {
				/^scale *= ((com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon) entity).getMobScaling();  // randommobsizes version < 2.0
				^///? }
			} catch (Exception | LinkageError e) {
				isNoRandomMobSizes = true;
			}
		}
		*///? }

		if (entity.isBaby())
			scale *= 0.66f;
		scale *= entity.getScale();
		return scale;
	}
}
