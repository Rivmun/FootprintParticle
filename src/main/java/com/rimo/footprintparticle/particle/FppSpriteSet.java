package com.rimo.footprintparticle.particle;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if > 1.18.2 {
import net.minecraft.util.RandomSource;
//? } else {
/*import java.util.Random;
*///? }

import java.util.List;
import java.util.function.Supplier;

/**
 * 自托管粒子专用的 {@link SpriteSet} 实现。
 *
 * <p>本项目不再把粒子类型注册进原版 {@code minecraft:particle_type}（那是被 loader 同步给连接方的同步表，
 * 会让未装模组的客户端连接时因“存在未注册的粒子类型”被踢线）。因此也不再依赖原版
 * {@code ParticleEngine$MutableSpriteSet} / {@code ParticleResources$MutableSpriteSet} 与 Fabric 的
 * {@code FabricSpriteProvider}。</p>
 *
 * <p>本类<b>不缓存</b> {@link TextureAtlasSprite}，而是持有一个 {@link Supplier}：每次取 sprite 时现向粒子图集
 * 查询（见 {@link FppSpriteAtlas}）。这样既不需要在图集缝合后手动回填，也能在 F3+T
 * 资源重载后自动拿到重新缝合的 sprite（旧 sprite 对象会失效），从根本上回避了填充时序与重载失效问题。</p>
 *
 * <p>各版本 {@code SpriteSet} 接口的抽象方法集不同：1.16.5 用 {@code java.util.Random}、
 * 1.18.2~1.21.1 用 {@code RandomSource}、1.21.11+ 额外多一个 {@code first()}；下面用 Stonecutter
 * 逐版本补齐，只实现当前版本接口真正要求的方法。</p>
 */
public class FppSpriteSet implements SpriteSet {
	private final Supplier<List<TextureAtlasSprite>> resolver;

	public FppSpriteSet(Supplier<List<TextureAtlasSprite>> resolver) {
		this.resolver = resolver;
	}

	/** 供 {@link com.rimo.footprintparticle.Util#getCustomSprites} 按名字匹配自定义贴图。 */
	public List<TextureAtlasSprite> sprites() {
		return this.resolver.get();
	}

	@Override
	public TextureAtlasSprite get(int index, int total) {
		List<TextureAtlasSprite> sprites = this.resolver.get();
		if (sprites.isEmpty())
			return null;
		int size = sprites.size();
		if (size == 1)
			return sprites.get(0);
		// 复刻原版 ParticleEngine$MutableSpriteSet.get(int,int)：index * (size-1) / total。
		// setSpriteFromAge 传 (age, lifetime)，于是贴图随生命进度在整个 lifetime 上均匀推进（按 life 阶段缓慢切换），
		// 而非每 tick 变帧。旧实现 (x + y) % size 会因 age 每 tick+1 而快速循环。
		return sprites.get(Math.abs(index) * (size - 1) / (total <= 0 ? 1 : total));
	}

	//? if > 1.18.2 {
	@Override
	public TextureAtlasSprite get(RandomSource random) {
		List<TextureAtlasSprite> sprites = this.resolver.get();
		if (sprites.isEmpty())
			return null;
		return sprites.get(random.nextInt(sprites.size()));
	}
	//? } else {
	/*@Override
	public TextureAtlasSprite get(Random random) {
		List<TextureAtlasSprite> sprites = this.resolver.get();
		if (sprites.isEmpty())
			return null;
		return sprites.get(random.nextInt(sprites.size()));
	}
	*///? }

	//? if >= 1.21.11 {
	@Override
	public TextureAtlasSprite first() {
		List<TextureAtlasSprite> sprites = this.resolver.get();
		return sprites.isEmpty() ? null : sprites.get(0);
	}
	//? }
}
