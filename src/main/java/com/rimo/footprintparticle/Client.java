package com.rimo.footprintparticle;

import com.rimo.footprintparticle.config.Config;
import com.rimo.footprintparticle.particle.*;
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
 * <p>粒子类型对象在此 <em>构造</em> 为 public static final 单例（同时充当 ParticleOptions）。
 * 它们 <b>不</b> 注册进原版 {@code particle_type} 同步表——那样会被 loader 同步给连接方、踢掉未装模组的客户端。
 * 真正的"注册"由共享 {@link FppParticleRegistry} 私有持有 provider/SpriteSet，
 * 运行期经共享 {@code ParticleEngineMixin} 在 {@code makeParticle} HEAD 拦截直接造粒子。</p>
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
		// 粒子自托管由 FppParticleRegistry 静态初始化完成；此处保留为共享初始化钩子。
	}
}
