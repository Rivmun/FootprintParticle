//? if neoforge {
/*package com.rimo.footprintparticle.loaders.neoforge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.particle.FppParticleRegistry;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.config.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.nio.file.Path;

// NeoForge 侧入口。 META-INF/neoforge.mods.toml 声明 modId，本类靠 @Mod 被 FML 实例化。
// 本项目是仅客户端粒子模组：@Mod(dist = Dist.CLIENT) 让整个入口类只在物理客户端加载，专用服务端不会实例化本类。
//
// 粒子已完全自托管（见 FppParticleRegistry 与共享 ParticleEngineMixin）：不再把 4 个 ParticleType 注册进
// 原版 Registries.PARTICLE_TYPE（NeoForge 会把它同步给连接方，未装本模组的客户端因此被踢线），也不再走
// RegisterParticleProvidersEvent.registerSpriteSet——provider 由我们的私有表持有，在 makeParticle HEAD 拦截时
// 直接使用。NeoForge 全部 target 均 ≥ 1.21.1，粒子图集是资源驱动的，自定义贴图会随 particles/*.json 自动缝合，
// 无需任何手动 addSprite。因此本类只剩平台能力注入（PLATFORM）与配置屏挂线。
@Mod(value = Client.MOD_ID, dist = Dist.CLIENT)
public class Platform {
	static {
		PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
			public boolean isModLoaded(String id) {
				return ModList.get().isLoaded(id);
			}

			public Path getConfigFolder() {
				return FMLPaths.CONFIGDIR.get();
			}
		};
	}

	public Platform() {
		// 粒子自托管，无需在此挂线；配置屏扩展点由下方 ClientEvents 处理。
	}

	@EventBusSubscriber(modid = Client.MOD_ID, value = Dist.CLIENT)
	public static class ClientEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			Client.init();
			if (ModList.get().isLoaded("cloth_config")) {
				ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
						container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) ->
								new ConfigScreen().build(parent)));
			}
		}
	}
}
*///? }
