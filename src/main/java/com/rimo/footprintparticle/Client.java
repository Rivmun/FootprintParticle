package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WaterSplashParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
//? if = 1.16.5 {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///? } else {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? }

/**
 * 仅客户端模组的共享入口：MOD_ID / 日志 / 配置单例 / 4 个粒子类型实例。
 *
 * <p>粒子类型对象在此仅 <em>构造</em>，真正的注册（写入 BuiltInRegistries.PARTICLE_TYPE）留给
 * {@code loaders.<平台>.Platform}——Fabric 用 {@code Registry.register}（走 {@code onInitializeClient}），
 * NeoForge 用 {@code RegisterEvent.register}（挂在 {@code @EventBusSubscriber(value = Dist.CLIENT)} 里）；
 * 两边再通过 lambda 引用同一批 {@code public static final} 实例。</p>
 */
public class Client {
	public static final String MOD_ID = "footprintparticle";
	//~if = 1.16.5 'LoggerFactory.' -> 'LogManager.'
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Config CONFIG = Config.load();

	// Our Particles...
	public static final FootprintParticleType FOOTPRINT = new FootprintParticleType(true);
	public static final WatermarkParticleType WATERMARK = new WatermarkParticleType(true);
	public static final SnowDustParticleType SNOWDUST = new SnowDustParticleType(true);
	public static final WaterSplashParticleType WATERSPLASH = new WaterSplashParticleType(true);

	public static void init() {
		// 平台差异化注册在 loaders.<平台>.Platform 里完成；此处留空作为共享初始化钩子。
	}
}
