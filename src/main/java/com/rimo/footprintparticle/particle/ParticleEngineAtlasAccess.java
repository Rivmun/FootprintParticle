package com.rimo.footprintparticle.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if >= 1.21.11 {
import net.minecraft.resources.Identifier;
//? } else {
/*import net.minecraft.resources.ResourceLocation;
*///? }

/**
 * 由共享 {@code ParticleEngineMixin} 实现：从 {@code ParticleEngine} 自己持有的粒子图集里取指定 sprite。
 *
 * <p>之所以不直接用 {@code Minecraft.getTextureAtlas(particlesId)}：1.20.1 / 1.21.1 的该方法只查
 * {@code ModelManager} 的 {@code AtlasSet}，而粒子图集是 {@code ParticleEngine} 自己 {@code new} 出来、
 * 并不在这个集合里，硬取会 {@code Map.get(id)} 返回 null 触发 NPE。改从引擎自持图集取才对。</p>
 *
 * <p>接口放在 root 包（非 mixin 受管包），使 {@link FppSpriteAtlas} 能从包外安全引用，不触发 IllegalClassLoadError。</p>
 */
public interface ParticleEngineAtlasAccess {
	//? if >= 1.21.11 {
	TextureAtlasSprite fpp$particleSprite(Identifier id);
	//? } else {
	/*TextureAtlasSprite fpp$particleSprite(ResourceLocation id);
	*///? }
}
