//? if neoforge {
/*package com.rimo.footprintparticle.loaders.neoforge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.VersionUtil;
import com.rimo.footprintparticle.config.ConfigScreen;
import com.rimo.footprintparticle.mixin.ParticleSpriteSetAccessor;
import com.rimo.footprintparticle.particle.FootprintParticle;
import com.rimo.footprintparticle.particle.SnowDustParticle;
import com.rimo.footprintparticle.particle.WaterSplashParticle;
import com.rimo.footprintparticle.particle.WatermarkParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.nio.file.Path;
import java.util.List;

// NeoForge 侧入口。 META-INF/neoforge.mods.toml 声明 modId，本类靠 @Mod 被 FML 实例化。
// 本项目是仅客户端粒子模组：@Mod(dist = Dist.CLIENT) 让整个入口类只在物理客户端加载，
// 专用服务端根本不会实例化本类，因此 static {} 里对 net.minecraft.client.* （SpriteSet /
// TextureAtlasSprite / ParticleSpriteSetAccessor）的引用也不会在服务端触发 NoClassDefFoundError。
// ParticleType 与 ParticleProvider 同样只在客户端注册，所以 RegisterEvent /
// RegisterParticleProvidersEvent / FMLClientSetupEvent 三个事件统一挂在
// @EventBusSubscriber(value = Dist.CLIENT) 下，服务端不会解析任何客户端类。
//
// 由于 NeoForge 未提供对于粒子 SpriteSet 成员的直接访问，
// 通过 mixin 注入 ParticleSpriteSetAccessor 的 Accessor 暴露 MutableSpriteSet 的 sprites 字段
// （运行时实例即该原生类，强转即可调用 fpp$getSprites()）；本类 <clinit> 把强转 lambda
// 注册给 PlatformUtil.PLATFORM，供公共代码 Util.getCustomSprites 调用。Fabric 侧走官方 FabricSpriteSet API，见 fabric/Platform。
@Mod(value = Client.MOD_ID, dist = Dist.CLIENT)
public class Platform {
	static {
		PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
			public List<TextureAtlasSprite> getSprites(SpriteSet spriteSet) {
				return ((ParticleSpriteSetAccessor) spriteSet).fpp$getSprites();
			}

			public boolean isModLoaded(String id) {
				return ModList.get().isLoaded(id);
			}

			public Path getConfigFolder() {
				return FMLPaths.CONFIGDIR.get();
			}
		};
	}

	public Platform() {
		// 本类已由 @Mod(dist = Dist.CLIENT) 限定为仅客户端加载，此处无需再手动 addListener；
		// 注册类事件均由下方 @EventBusSubscriber 自动路由。
	}

	@EventBusSubscriber(modid = Client.MOD_ID, value = Dist.CLIENT)
	public static class ClientEvents {
		@SubscribeEvent
		public static void onRegister(RegisterEvent event) {
			// 粒子类型注册：仅客户端，服务端不需要 PARTICLE_TYPE registry 条目。
			if (event.getRegistryKey().equals(Registries.PARTICLE_TYPE)) {
				event.register(Registries.PARTICLE_TYPE, VersionUtil.getId("footprint"), () -> Client.FOOTPRINT);
				event.register(Registries.PARTICLE_TYPE, VersionUtil.getId("watermark"), () -> Client.WATERMARK);
				event.register(Registries.PARTICLE_TYPE, VersionUtil.getId("snowdust"), () -> Client.SNOWDUST);
				event.register(Registries.PARTICLE_TYPE, VersionUtil.getId("watersplash"), () -> Client.WATERSPLASH);
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
			if (ModList.get().isLoaded("cloth_config")) {
				ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
						container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) ->
								new ConfigScreen().build(parent)));
			}
		}
	}
}
*///? }
