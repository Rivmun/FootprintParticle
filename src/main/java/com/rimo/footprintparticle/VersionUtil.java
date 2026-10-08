package com.rimo.footprintparticle;

//? if 1.16.5 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
*///? }
//~ if < 1.19.3 'net.minecraft.core.registries.Registries' -> 'net.minecraft.core.Registry'
import net.minecraft.core.registries.Registries;
//~ if < 1.21.11 '.Identifier' -> '.ResourceLocation'
import net.minecraft.resources.Identifier;
//? if > 1.18.2
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.Collections;

/**
 * Full of Stonecutter language here as you like...
 */
public final class VersionUtil {
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

	//~ if < 1.21.11 'Identifier' -> 'ResourceLocation'
	//~ if < 1.21.1 '.withDefaultNamespace' -> '.tryParse'
	//~ if < 1.19.3 'Registries.BLOCK' -> 'Registry.BLOCK_REGISTRY'
	//? if > 1.18.2
	private final ResourceKey<Block> fpp$AIR = ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("air"));

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
	/*public static Collection<ResourceLocation> getBlockTags(BlockState block) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection == null)
			return Collections.emptyList();
		return connection.getTags().getBlocks().getMatchingTags(block.getBlock());
	}
	*///? } else {
	public static List<TagKey<Block>> getBlockTags(BlockState block) {
		//~ if < 26.1 '.tags()' -> '.getTags()'
		return block.tags().toList();
	}
	//? }
}
