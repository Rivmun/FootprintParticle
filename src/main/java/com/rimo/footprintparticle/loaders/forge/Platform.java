//? if forge {
/*package com.rimo.footprintparticle.loaders.forge;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.particle.FppParticleRegistry;
import com.rimo.footprintparticle.PlatformUtil;
import com.rimo.footprintparticle.config.ConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
//? if ! 1.16.5 {
//~ if < 1.19.2 '.ConfigScreenHandler' -> '.ConfigGuiHandler'
import net.minecraftforge.client.ConfigScreenHandler;
//? } else {
/^import net.minecraftforge.fml.ExtensionPoint;
^///? }

import java.nio.file.Path;

// Forge（1.16.5~1.20.1）侧入口。 META-INF/mods.toml 声明 modId，本类靠 @Mod 被 FML 实例化。
//
// 粒子已完全自托管并与三端统一：4 个 ParticleType 不注册进原版 particle_type 同步表，provider 与 SpriteSet
// 由共享 FppParticleRegistry 私有持有，运行期由共享 ParticleEngineMixin 在 makeParticle HEAD 拦截直接造粒子。
// 因此本类不再需要 Forge 专用的 ForgeParticleRegistry / ParticleEngineLocal / mixin / provider 注入事件。
//
// 与 NeoForge 的差异：
// - Forge 的 @Mod 注解没有 dist 参数，入口类在专用服务端也会被实例化，因此把引用了客户端相关能力的
//   PLATFORM 初始化放进 @EventBusSubscriber(value = Dist.CLIENT) 内部类的静态块——该内部类只会在物理客户端被载入。
// - 配置屏扩展点是 ConfigScreenHandler.ConfigScreenFactory（IConfigScreenFactory 是 NeoForge 才有的）。
//
// ≤1.19.2 的粒子图集缝合是注册驱动的，未注册类型贴图不会被自动缝入，由 ForgeParticleStitcher（forge-only）
// 在 TextureStitchEvent.Pre 手动 addSprite；≥1.20.1 资源驱动，无需手动登记。
@Mod(Client.MOD_ID)
public class Platform {
	public Platform() {
		// 粒子自托管，无需在此挂线。
	}

	@Mod.EventBusSubscriber(modid = Client.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientEvents {
		static {
			PlatformUtil.PLATFORM = new PlatformUtil.IPlatform() {
				public boolean isModLoaded(String id) {
					return ModList.get().isLoaded(id);
				}

				public Path getConfigFolder() {
					return FMLPaths.CONFIGDIR.get();
				}
			};
			// ≤1.19.2-forge：TextureStitchEvent 属加载期事件，在 Forge 派发于 MOD 总线（挂 game 总线 /
			// MinecraftForge.EVENT_BUS 与 bus=FORGE 实测都无回调）。故把缝合监听器注册到本 mod 的 MOD 总线；
			// register(Class) 依方法上的 @SubscribeEvent 解析事件类型。
			//? if forge && <= 1.19.2 {
			/^net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus()
					.register(ForgeParticleStitcher.class);
			^///? }
		}

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			// 触发共享登记表加载（构造 provider 与 SpriteSet）。
			FppParticleRegistry.particlePaths();
			Client.init();
			//~ if 1.16.5 'cloth_config' -> 'cloth-config'
			if (ModList.get().isLoaded("cloth_config")) {
				ModList.get().getModContainerById(Client.MOD_ID).ifPresent(container ->
						container.registerExtensionPoint(
								//? if ! 1.16.5 {
								//~ if < 1.19.2 'ConfigScreenHandler.ConfigScreenFactory' -> 'ConfigGuiHandler.ConfigGuiFactory' {
								ConfigScreenHandler.ConfigScreenFactory.class,
								() -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) ->
										new ConfigScreen().build(parent))));
								//~}
								//?} else {
								/^ExtensionPoint.CONFIGGUIFACTORY,
								() -> (client, parent) ->
										new ConfigScreen().build(parent)));
								^///?}
			}
		}
	}
}
*///? }
