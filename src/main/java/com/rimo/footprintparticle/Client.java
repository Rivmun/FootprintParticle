package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WaterSplashParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 仅客户端模组的共享入口：MOD_ID / 日志 / 配置单例 / 4 个粒子类型实例。
 *
 * <p>粒子类型对象在此仅 <em>构造</em>，真正的注册（写入 BuiltInRegistries.PARTICLE_TYPE）留给
 * {@code loaders.<平台>.Platform}——Fabric 用 {@code Registry.register}（走 {@code onInitializeClient}），
 * NeoForge 用 {@code RegisterEvent.register}（挂在 {@code @EventBusSubscriber(value = Dist.CLIENT)} 里）；
 * 两边再通过 lambda 引用同一批 {@code public static final} 实例。</p>
 *
 * <p>{@link #CONFIG} 在类 {@code <clinit>} 里调 {@code AutoConfig.register}，因此任何
 * mixin/particle 首次读 {@code Client.CONFIG} 时会自动完成注册；{@code AutoConfigClient} 之后
 * 用 {@code FPPConfig.class} 找配置屏也依赖这一步先跑过。</p>
 */
public class Client {
	public static final String MOD_ID = "footprintparticle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Config CONFIG = AutoConfig.register(Config.class, GsonConfigSerializer::new).getConfig();

	public static final FootprintParticleType FOOTPRINT = new FootprintParticleType(true);
	public static final WatermarkParticleType WATERMARK = new WatermarkParticleType(true);
	public static final SnowDustParticleType SNOWDUST = new SnowDustParticleType(true);
	public static final WaterSplashParticleType WATERSPLASH = new WaterSplashParticleType(true);

	public static void init() {
		// 平台差异化注册在 loaders.<平台>.Platform 里完成；此处留空作为共享初始化钩子。
	}
}
