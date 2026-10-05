package com.rimo.footprintparticle.mixin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Mixin {@code @Accessor} 直接暴露原版 SpriteSet 实现内部的 {@code sprites} 字段。
 *
 * <p>26.x 里 {@code SpriteSet} 的运行时唯一实现是包私有嵌套类
 * {@code net.minecraft.client.particle.ParticleResources$MutableSpriteSet}（源码级不可见），
 * 因此 target 用字符串形式；字段名保持 {@code sprites}（{@code List<TextureAtlasSprite>}）。</p>
 *
 * <p>Fabric 侧接管了原版 SpriteSet 实现，运行时粒子内部不使用该类，所以此处目前只由 Neoforge 侧调用。</p>
 */
@Mixin(targets = "net.minecraft.client.particle.ParticleResources$MutableSpriteSet")
public interface ParticleSpriteSetAccessor {
	@Accessor("sprites")
	List<TextureAtlasSprite> fpp$getSprites();
}
