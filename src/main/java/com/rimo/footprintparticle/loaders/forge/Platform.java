//? if forge {
/*package com.rimo.footprintparticle.loaders.forge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.mixin.ParticleSpriteSetAccessor;
import com.rimo.footprintparticle.particle.*;
//~ if < 1.21.11 '.AutoConfigClient' -> '.AutoConfig'
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
//~ if < 1.19.2 'RegisterParticleProvidersEvent' -> 'ParticleFactoryRegisterEvent'
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//? if ! 1.16.5 {
//~ if < 1.19.2 '.ConfigScreenHandler' -> '.ConfigGuiHandler'
import net.minecraftforge.client.ConfigScreenHandler;
//? } else {
/^import net.minecraftforge.fml.ExtensionPoint;
^///? }

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
		// 粒子类型对象在共享 Client 里构造；Forge 侧的私有登记由 ForgeParticleRegistry（静态映射）
		// 完成，provider 注入由下方 onRegisterParticleProviders 完成，构造器无需额外挂线。
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
			//? if forge&& <= 1.19.2 {
			/^// ≤1.19.2-forge：TextureStitchEvent 属加载期事件，在 Forge 派发于 MOD 总线（挂 game 总线 /
			// MinecraftForge.EVENT_BUS 与 bus=FORGE 实测都无回调）。故把缝合/回填监听器注册到本 mod 的
			// MOD 总线；register(Class) 依方法上的 @SubscribeEvent 解析事件类型，比方法引用更稳。
			net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus()
					.register(ForgeParticleAtlas.class);
			^///? }
		}

		// 不再走原版 RegisterEvent / RegisterParticleProvidersEvent.registerSpriteSet：那两条路径
		// 都依赖会被 server→client 同步重建的原版 minecraft:particle_type 表，纯客户端条目登录后即失效。
		// 改由 ForgeParticleRegistry 的私有不同步映射 + ParticleEngine 本地注入实现。
		@SubscribeEvent
		//~ if < 1.19.2 'RegisterParticleProvidersEvent' -> 'ParticleFactoryRegisterEvent'
		public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
			// 该事件在客户端 ParticleEngine 已构造后派发，此处借其时序把 provider 直接注入引擎内部表；
			// 引擎实例由 ParticleEngineMixin 混入 ParticleEngineLocal 接口方法 fpp$registerLocal。
			ParticleEngineLocal engine = (ParticleEngineLocal) (Object) Minecraft.getInstance().particleEngine;
			engine.fpp$registerLocal(ForgeParticleRegistry.FOOTPRINT, FootprintParticle.DefaultFactory::new);
			engine.fpp$registerLocal(ForgeParticleRegistry.WATERMARK, WatermarkParticle.DefaultFactory::new);
			engine.fpp$registerLocal(ForgeParticleRegistry.SNOWDUST, SnowDustParticle.DefaultFactory::new);
			engine.fpp$registerLocal(ForgeParticleRegistry.WATERSPLASH, WaterSplashParticle.DefaultFactory::new);
		}

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			Client.init();
			//? if ! 1.16.5 {
			ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
					//~ if < 1.19.2 'ConfigScreenHandler.ConfigScreenFactory' -> 'ConfigGuiHandler.ConfigGuiFactory' {
					container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
							() -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> {
								//~ if < 1.21.11 'AutoConfigClient' -> 'AutoConfig'
								var screen = AutoConfigClient.getConfigScreen(Config.class, parent).get();
								if (screen == null)  // 配置屏异常时兜底返回原版游戏屏，避免 Forge 直接崩溃
									screen = Minecraft.getInstance().screen;
								return screen;
							})
					)
					//~ }
			);
			//? } else {
			/^ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
					container.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
							() -> (client, parent) -> {
								return AutoConfig.getConfigScreen(Config.class, parent).get();
							}
					)
			);
			^///? }
		}
	}
}
*///? }
