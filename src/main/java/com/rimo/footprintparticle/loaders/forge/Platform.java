//? if forge {
/*package com.rimo.footprintparticle.loaders.forge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.Util;
import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.mixin.ParticleSpriteSetAccessor;
import com.rimo.footprintparticle.particle.*;
//~ if < 1.21.11 '.AutoConfigClient' -> '.AutoConfig'
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;

// Forge（1.20.1）侧入口。 META-INF/mods.toml 声明 modId，本类靠 @Mod 被 FML 实例化。
// 与 NeoForge 的差异：
// - Forge 的 @Mod 注解没有 dist 参数，入口类在专用服务端也会被实例化，因此不能像
//   neoforge/Platform 那样在外部类 <clinit> 里引用客户端类（SpriteSet / ParticleSpriteSetAccessor），
//   否则服务端加载本类时可能触发 NoClassDefFoundError；
//   PLATFORM 的初始化放进 @EventBusSubscriber(value = Dist.CLIENT) 的内部类静态块，
//   该内部类只会在物理客户端被 FML 扫描载入。
// - 配置屏扩展点是 ConfigScreenHandler.ConfigScreenFactory（IConfigScreenFactory 是 NeoForge 20.6+ 才有的）。
//
// Sprite 枚举能力与 NeoForge 侧相同：Forge 未提供对粒子 SpriteSet 成员的直接访问，
// 运行时实例是原版 ParticleEngine$MutableSpriteSet，靠 mixin ParticleSpriteSetAccessor
// 暴露的 fpp$getSprites() 取全部 sprite，供公共代码 Util.getCustomSprites 调用。
@Mod(Client.MOD_ID)
public class Platform {
	public Platform() {
		// 注册类事件均由下方 ClientEvents（mod bus）自动路由，构造器无需 addListener。
	}

	@Mod.EventBusSubscriber(modid = Client.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientEvents {
		static {
			PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
				@Override
				public List<TextureAtlasSprite> getSprites(SpriteSet spriteSet) {
					// 此处在 1.21.1 版本下会有个 Mixin class cannot be referenced directly 的警告，但实测不影响编译和运行，暂不理会。
					return ((ParticleSpriteSetAccessor) spriteSet).fpp$getSprites();
				}

				@Override
				public boolean isModLoaded(String id) {
					return ModList.get().isLoaded(id);
				}
			};
		}

		@SubscribeEvent
		public static void onRegister(RegisterEvent event) {
			// 粒子类型注册：仅客户端，服务端不需要 PARTICLE_TYPE registry 条目。
			if (event.getRegistryKey().equals(Registries.PARTICLE_TYPE)) {
				event.register(Registries.PARTICLE_TYPE, Util.getId("footprint"), () -> Client.FOOTPRINT);
				event.register(Registries.PARTICLE_TYPE, Util.getId("watermark"), () -> Client.WATERMARK);
				event.register(Registries.PARTICLE_TYPE, Util.getId("snowdust"), () -> Client.SNOWDUST);
				event.register(Registries.PARTICLE_TYPE, Util.getId("watersplash"), () -> Client.WATERSPLASH);
			}
		}

		@SubscribeEvent
		public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(Client.FOOTPRINT, FootprintParticle.DefaultFactory::new);
			event.registerSpriteSet(Client.WATERMARK, WatermarkParticle.DefaultFactory::new);
			event.registerSpriteSet(Client.SNOWDUST, SnowDustParticle.DefaultFactory::new);
			event.registerSpriteSet(Client.WATERSPLASH, WaterSplashParticle.DefaultFactory::new);
		}

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			Client.init();
			// 注册配置屏扩展点：Forge 内建模组列表只有在注册后才显示「Config」按钮，
			// 相当于 Fabric 侧 ModMenuIntegration 的替代。AutoConfig 静态 API 由 cloth-config (forge) 提供。
			ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
					container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
							() -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> {
								//~ if < 1.21.11 'AutoConfigClient' -> 'AutoConfig'
								var screen = AutoConfigClient.getConfigScreen(Config.class, parent).get();
								// 配置屏异常时兜底返回原版游戏屏，避免 Forge 直接崩溃
								if (screen == null)
									screen = Minecraft.getInstance().screen;
								return screen;
							})
					)
			);
		}
	}
}
*///? }
