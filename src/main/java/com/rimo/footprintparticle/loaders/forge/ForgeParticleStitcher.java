//? if forge && <= 1.19.2 {
/*package com.rimo.footprintparticle.loaders.forge;

import com.rimo.footprintparticle.particle.FppSpriteAtlas;
import com.rimo.footprintparticle.particle.FppParticleRegistry;
import com.rimo.footprintparticle.VersionUtil;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// 仅 forge 且 MC ≤ 1.19.2 生效的粒子图集缝合（Stonecutter 双条件：forge + ≤1.19.2）。
//
// 本模组把 4 个纯客户端 ParticleType 放在私有不同步表里、不注册进原版 particle_type。≤1.19.2 的粒子图集
// 缝合是注册驱动的——未注册类型的贴图不会被自动缝入，导致 footprint / watermark 取不到真实 sprite。
// 这里在 TextureStitchEvent.Pre 阶段读取（可能被资源包覆写的）particles/<id>.json 的贴图列表并 addSprite，
// 把它们补进 particles 图集；运行期取回由共享 FppSpriteSet 的惰性 resolver 现向图集查询（无需在此回填，
// 也无需再持有 MutableSpriteSet）。监听器由 Platform.ClientEvents 静态块注册到本 mod 的 MOD 事件总线。
// （≥1.20.1 粒子图集是资源驱动的，原版会枚举所有 particles/^.json 缝合，故本类不参与。）
public class ForgeParticleStitcher {
	//~ if 1.16.5 '.getAtlas()' -> '.getMap()' {
	@SubscribeEvent
	public static void onStitchPre(TextureStitchEvent.Pre event) {
		if (!event.getAtlas().location().equals(VersionUtil.getId("minecraft", "textures/atlas/particles.png")))
			return;
		FppSpriteAtlas.forEachStitchTexture(FppParticleRegistry.particlePaths(),
				(namespace, path) -> event.addSprite(VersionUtil.getId(namespace, path)));
	}
	//~ }
}
*///? }
