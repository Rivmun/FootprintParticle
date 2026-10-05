//? if neoforge {
/*package com.rimo.footprintparticle.loaders.neoforge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.mixin.ParticleSpriteSetAccessor;
import com.rimo.footprintparticle.particle.FootprintParticle;
import com.rimo.footprintparticle.particle.SnowDustParticle;
import com.rimo.footprintparticle.particle.WaterSplashParticle;
import com.rimo.footprintparticle.particle.WatermarkParticle;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.RegisterEvent;

// NeoForge 侧入口。 META-INF/neoforge.mods.toml 声明 modId，本类靠 @Mod 被 FML 实例化。
// 本项目是仅客户端粒子模组：ParticleType 与 ParticleProvider 都只在客户端注册，
// 所以 RegisterEvent / RegisterParticleProvidersEvent / FMLClientSetupEvent 三个事件
// 统一挂在 @EventBusSubscriber(value = Dist.CLIENT) 下，服务端不会解析任何客户端类。
//
// 由于 NeoForge 未提供对于粒子 SpriteSet 成员的直接访问，
// Sprite 枚举能力通过 mixin ParticleSpriteSetAccessor 暴露 @Accessor("sprites") 生成的
// fpp$getSprites()；本类 <clinit> 把 accessor 强转 lambda 注册给 PlatformUtil.PLATFORM，
// 供公共代码 Util.getCustomSprites 调用。Fabric 侧则走官方 FabricSpriteSet API，见 fabric/Platform。
@Mod(Client.MOD_ID)
public class Platform {
	static {
		// NeoForge 侧 SpriteSet 实例即原版 ParticleResources$MutableSpriteSet，mixin accessor 直接命中。
		PlatformUtil.PLATFORM = spriteSet -> ((ParticleSpriteSetAccessor) spriteSet).fpp$getSprites();
	}

	public Platform() {
		// @Mod 构造器双端都会跑，无需再手动 addListener；事件由 @EventBusSubscriber 自动路由。
	}

	@EventBusSubscriber(modid = Client.MOD_ID, value = Dist.CLIENT)
	public static class ClientEvents {
		@SubscribeEvent
		public static void onRegister(RegisterEvent event) {
			// 粒子类型注册：仅客户端，服务端不需要 PARTICLE_TYPE registry 条目。
			if (event.getRegistryKey().equals(Registries.PARTICLE_TYPE)) {
				event.register(Registries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(Client.MOD_ID, "footprint"), () -> Client.FOOTPRINT);
				event.register(Registries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(Client.MOD_ID, "watermark"), () -> Client.WATERMARK);
				event.register(Registries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(Client.MOD_ID, "snowdust"), () -> Client.SNOWDUST);
				event.register(Registries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(Client.MOD_ID, "watersplash"), () -> Client.WATERSPLASH);
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
			// 注册配置屏扩展点：NeoForge 内建模组列表只有在注册后才显示「Config」按钮，
			// 相当于 Fabric 侧 ModMenuIntegration 的替代。AutoConfigClient 由 cloth-config-neoforge 提供。
			ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
					container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) ->
							AutoConfigClient.getConfigScreen(Config.class, parent).get()));
		}
	}
}
*///? }
