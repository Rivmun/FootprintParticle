//? if forge&& <= 1.19.2 {
/*package com.rimo.footprintparticle.loaders.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rimo.footprintparticle.mixin.ParticleSpriteSetAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 仅 forge 且 MC <= 1.19.2 生效的粒子图集整合（Stonecutter 双条件：forge + <=1.19.2）。
//
// 背景：本模组把 4 个纯客户端 ParticleType 放在私有不同步表里、不注册进原版 particle_type，
// 运行期由 ParticleEngineMixin 把 provider 与一个空的 MutableSpriteSet 直接塞进 ParticleEngine。
// 1.20.1 起，粒子图集烘焙会枚举 assets/^^/particles/^.json 描述并把贴图 setSprites 回填到 spriteSets
// 里同 id 的 set（故我们的空 set 会自动拿到真实贴图）；但 <=1.19.2 的贴图收集是注册表驱动的，
// 我们未注册的类型既不会被缝合、set 也不会被回填 —— sprites 字段保持 null，Util.getCustomSprites
// 对其 .stream() 触发 NPE，脚印/水痕不显示。
//
// 该类补上这一步：Pre 阶段读取（可能被资源包覆写的）particles/<id>.json 的 textures 列表，把每张
// 贴图 addSprite 进 particles 图集；Post 阶段再按同一列表从图集取 sprite 回填我们注入的 MutableSpriteSet。
// 读取描述文件（而非硬编码贴图名）保证用户资源包自定义印花照常生效。
// 监听器由 Platform.ClientEvents 静态块注册到本 mod 的 MOD 事件总线（TextureStitchEvent 属加载期事件，
// 在 Forge 派发于 MOD 总线；挂 game 总线 / MinecraftForge.EVENT_BUS 实测不会回调）。
public class ForgeParticleAtlas {
	// 粒子图集 id：1.18.2/1.19.2 为 SpriteUploader.PARTICLES，字符串即 minecraft:textures/atlas/particles.png。
	private static final ResourceLocation PARTICLES_ATLAS = new ResourceLocation("textures/atlas/particles.png");

	@SubscribeEvent
	public static void onStitchPre(TextureStitchEvent.Pre event) {
		if (!event.getAtlas().location().equals(PARTICLES_ATLAS))
			return;
		for (ResourceLocation id : ForgeParticleRegistry.localIds())
			for (ResourceLocation texture : descriptionTextures(id))
				event.addSprite(texture);
	}

	@SubscribeEvent
	public static void onStitchPost(TextureStitchEvent.Post event) {
		if (!event.getAtlas().location().equals(PARTICLES_ATLAS))
			return;
		TextureAtlas atlas = event.getAtlas();
		for (ResourceLocation id : ForgeParticleRegistry.localIds()) {
			ParticleEngine.MutableSpriteSet set = ForgeParticleRegistry.spriteSetOf(id);
			if (set == null)
				continue;
			List<TextureAtlasSprite> sprites = new ArrayList<>();
			for (ResourceLocation texture : descriptionTextures(id))
				sprites.add(atlas.getSprite(texture));
			if (!sprites.isEmpty())
				((ParticleSpriteSetAccessor) set).fpp$setSprites(sprites);
		}
	}

	// ≤​1.19.2：粒子图集 sprite 名 = 相对 textures/ 的完整路径，必须含 particle/ 子目录（1.20.1 现代格式会
	// 自动补，​1.19.2 的原始 addSprite 不会）。json 里写 "ns:footprint"，实际文件在
	// assets/ns/textures/particle/footprint.png，故补成 "ns:particle/footprint"；已含前缀则不变。
	private static ResourceLocation toParticleAtlasSprite(ResourceLocation tex) {
		String path = tex.getPath();
		if (path.startsWith("particle/"))
			return tex;
		return new ResourceLocation(tex.getNamespace(), "particle/" + path);
	}

	// 读取资源包里 assets/<ns>/particles/<path>.json 的 textures 列表（含资源包覆写版本，保留自定义印花）。
	// 元素可能是纯字符串，或 {"type":"...","file":"ns:path"} 对象，两种都要处理。解析失败返回空列表。
	private static List<ResourceLocation> descriptionTextures(ResourceLocation particleId) {
		List<ResourceLocation> out = new ArrayList<>();
		ResourceLocation json = new ResourceLocation(
				particleId.getNamespace(), "particles/" + particleId.getPath() + ".json");
		// 整体 try 包裹：≤ 1.18.2 的 getResource 直接返回 Resource 且缺资源时抛 IOException，需在此吞掉；
		// 1.19.2 返回 Optional 并改用 open() 读流。两版本靠下面两条替换指令归一为同一形态。
		try {
			//~ if < 1.19.2 'Minecraft.getInstance().getResourceManager().getResource(json);' -> 'Optional.of(Minecraft.getInstance().getResourceManager().getResource(json));'
			Optional<Resource> optional = Optional.of(Minecraft.getInstance().getResourceManager().getResource(json));
			if (optional.isEmpty())
				return out;
			//~ if < 1.19.2 '.open()' -> '.getInputStream()'
			try (InputStream is = optional.get().getInputStream();
			     InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				JsonElement root = JsonParser.parseReader(reader);
				JsonArray textures = root.getAsJsonObject().getAsJsonArray("textures");
				if (textures == null)
					return out;
				for (JsonElement el : textures) {
					String name = el.isJsonObject()
							? el.getAsJsonObject().get("file").getAsString()
							: el.getAsString();
					out.add(toParticleAtlasSprite(new ResourceLocation(name)));
				}
			}
		} catch (Exception ignored) {}
		return out;
	}
}
*///? }
