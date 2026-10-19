package com.rimo.footprintparticle.particle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.VersionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 自托管粒子的图集访问工具（跨 Fabric / Forge / NeoForge 共享，位于 root 包而非 loaders 包）。
 *
 * <p>本项目不再把粒子类型注册进原版 {@code particle_type}，也就不再由原版为它们缝合 / 回填 sprite。
 * 于是贴图来源分两种情况：</p>
 * <ul>
 *   <li><b>&ge; 1.20.1</b>：粒子图集是<em>资源驱动</em>的——原版会枚举 {@code assets/<ns>/particles/*.json}
 *   描述文件把其中列出的贴图一并缝进 particles 图集，与类型是否注册无关。我们只需在运行期按描述文件
 *   从图集<em>现取</em>对应 sprite（{@link #resolve}）。</li>
 *   <li><b>&le; 1.19.2</b>：粒子图集是<em>注册驱动</em>的——未注册类型的贴图不会被缝合，须由各加载器在图集
 *   缝合前把 {@link #forEachStitchTexture} 枚举出的贴图 addSprite 进去；取回仍走 {@link #resolve}。</li>
 * </ul>
 *
 * <p>本类不缓存 {@link TextureAtlasSprite} 对象（旧对象在 F3+T 重载后会失效），只缓存"描述文件里读到的
 * 贴图路径字符串"这一轻量结果；sprite 每次现取。</p>
 */
public final class FppSpriteAtlas {
	// path（footprint / watermark / ...）-> 该粒子描述文件里的贴图 id 列表（形如 "ns:path"，≤1.19.2 已补 particle/ 前缀）。
	private static final Map<String, List<String>> TEXTURE_CACHE = new HashMap<>();

	private FppSpriteAtlas() {
	}

	/** 读取（可能被资源包覆写的）描述文件贴图列表，并按需补 {@code particle/} 前缀（≤1.19.2）。结果缓存。 */
	public static List<String> descriptionTextures(String particlePath) {
		return TEXTURE_CACHE.computeIfAbsent(particlePath, FppSpriteAtlas::readDescription);
	}

	/**
	 * 现向 particles 图集取回 {@code particlePath} 对应的全部 sprite。由 {@link com.rimo.footprintparticle.particle.FppSpriteSet}
	 * 的 resolver 每次调用，从而对资源重载透明。
	 */
	public static List<TextureAtlasSprite> resolve(String particlePath) {
		List<TextureAtlasSprite> sprites = new ArrayList<>();
		// 粒子图集由 ParticleEngine 自持（不在 ModelManager 的 AtlasSet 里），故经共享 mixin 暴露的接口现取，
		// 而不是 Minecraft.getTextureAtlas —— 后者在 1.20.1/1.21.1 查不到粒子图集会 NPE。
		ParticleEngineAtlasAccess engine = (ParticleEngineAtlasAccess) Minecraft.getInstance().particleEngine;
		for (String texture : descriptionTextures(particlePath)) {
			String[] nsPath = splitNamespacePath(texture);
			sprites.add(engine.fpp$particleSprite(VersionUtil.getId(nsPath[0], nsPath[1])));
		}
		return sprites;
	}

	/**
	 * 枚举本模组全部粒子（{@code particlePaths}）在图集中应有的贴图，逐张以 (namespace, path) 回调。
	 * 供 &le;1.19.2 的加载器在图集缝合前 addSprite（注册驱动缝合需要手动登记未注册类型的贴图）。
	 */
	public static void forEachStitchTexture(Iterable<String> particlePaths, BiConsumer<String, String> adder) {
		for (String particlePath : particlePaths)
			for (String texture : descriptionTextures(particlePath)) {
				String[] nsPath = splitNamespacePath(texture);
				adder.accept(nsPath[0], nsPath[1]);
			}
	}

	private static List<String> readDescription(String particlePath) {
		List<String> out = new ArrayList<>();
		try {
			//? if < 1.19.2 {
			/*InputStream stream = Minecraft.getInstance().getResourceManager()
					.getResource(VersionUtil.getId(Client.MOD_ID, "particles/" + particlePath + ".json"))
					.getInputStream();
			*///? } else {
			java.util.Optional<Resource> optional = Minecraft.getInstance().getResourceManager()
					.getResource(VersionUtil.getId(Client.MOD_ID, "particles/" + particlePath + ".json"));
			InputStream stream = optional.isEmpty() ? null : optional.get().open();
			//? }
			if (stream == null)
				return out;
			try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
				JsonElement root = new JsonParser().parse(reader);
				JsonArray textures = root.getAsJsonObject().getAsJsonArray("textures");
				if (textures == null)
					return out;
				for (JsonElement element : textures) {
					String name = element.isJsonObject()
							? element.getAsJsonObject().get("file").getAsString()
							: element.getAsString();
					out.add(toAtlasTexture(name));
				}
			}
		} catch (Exception ignored) {
			// 描述文件缺失/解析失败：返回空列表，粒子将取不到 sprite（不崩溃）。
		}
		return out;
	}

	// ≤1.19.2：particles 图集里的 sprite 名带 particle/ 子目录（现代格式会自动补，原始格式不会）；
	// 已含前缀则不变。json 里 "footprintparticle:footprint" → "footprintparticle:particle/footprint"。
	private static String toAtlasTexture(String texture) {
		//? if <= 1.19.2 {
		/*int colon = texture.indexOf(':');
		String namespace = colon < 0 ? "minecraft" : texture.substring(0, colon);
		String path = colon < 0 ? texture : texture.substring(colon + 1);
		if (!path.startsWith("particle/"))
			path = "particle/" + path;
		return namespace + ":" + path;
		*///? } else {
		return texture;
		//? }
	}

	private static String[] splitNamespacePath(String texture) {
		int colon = texture.indexOf(':');
		if (colon < 0)
			return new String[]{"minecraft", texture};
		return new String[]{texture.substring(0, colon), texture.substring(colon + 1)};
	}
}
