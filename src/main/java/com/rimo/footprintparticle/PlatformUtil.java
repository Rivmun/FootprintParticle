package com.rimo.footprintparticle;

import java.nio.file.Path;

/**
 * 跨加载器能力抽象。各 {@code loaders.<平台>.Platform} 在类 {@code <clinit>} 里把实现
 * 赋给 {@link #PLATFORM} 单例；公共代码只通过本接口访问平台差异，避免直接依赖加载器特定方法。
 *
 * <p>粒子改为完全自托管后不再需要从原版 {@code SpriteSet} 反查 sprite 列表——自定义贴图匹配直接读
 * {@link com.rimo.footprintparticle.particle.FppSpriteSet#sprites()}，故本接口只剩两项纯加载器差异能力。</p>
 */
public final class PlatformUtil {
	/** 当前加载器的实现；由 loaders.&lt;平台&gt;.Platform 静态初始化块赋值。 */
	public static IPlatform PLATFORM;

	private PlatformUtil() {
	}

	public interface IPlatform {
		boolean isModLoaded(String id);
		Path getConfigFolder();
	}
}
