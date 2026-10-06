package com.rimo.footprintparticle;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

/**
 * 跨加载器能力抽象。各 {@code loaders.<平台>.Platform} 在类 {@code <clinit>} 里把实现
 * 赋给 {@link #PLATFORM} 单例；公共代码只通过本接口访问平台差异，避免直接依赖加载器特定方法。
 */
public final class PlatformUtil {
	/** 当前加载器的实现；由 loaders.&lt;平台&gt;.Platform 静态初始化块赋值。 */
	public static IPlatform PLATFORM;

	private PlatformUtil() {
	}

	public interface IPlatform {
		/**
		 * 列出 SpriteSet 加载的全部 sprite，供 {@link Util#getCustomSprites} 按名字匹配自定义贴图。
		 *
		 * <p>Fabric 侧：运行时 {@code SpriteSet} 实例是 fabric-api 的 {@code FabricSpriteSetImpl}
		 * （{@code MutableSpriteSet} 的 wrapper），它 implements 官方 {@code FabricSpriteSet}，直接强转拿
		 * {@code getSprites()}。</p>
		 *
		 * <p>NeoForge 侧：{@code SpriteSet} 实例就是原版 {@code MutableSpriteSet}，走 mixin
		 * {@code ParticleSpriteSetAccessor} 的 {@code fpp$getSprites()}（{@code @Accessor("sprites")} 生成）。</p>
		 */
		List<TextureAtlasSprite> getSprites(SpriteSet spriteSet);
		boolean isModLoaded(String id);
	}
}
