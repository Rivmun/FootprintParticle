package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.particle.FppParticleRegistry;
import com.rimo.footprintparticle.particle.ParticleEngineAtlasAccess;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 1.21.11 {
import com.rimo.footprintparticle.VersionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
//? } else {
/*import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
*///? }

/**
 * 跨加载器共享的粒子劫持（Fabric / Forge / NeoForge 三端统一）。
 *
 * <p>本模组的 4 个纯客户端粒子类型不注册进原版 {@code minecraft:particle_type}（同步表），因此原版
 * {@code ParticleEngine.makeParticle} 里那句 {@code providers.get(Registries.PARTICLE_TYPE.getKey(type.getType()))}
 * 对我们一定拿到 null key、查不到 provider，粒子生不出来。这里在 {@code makeParticle} 的 HEAD 上自己完成
 * 一次 provider 解析：命中 {@link FppParticleRegistry} 的私有表就用我们自己的 provider 造出粒子并
 * {@code setReturnValue} 取消原方法；不命中（所有非本模组粒子）直接 return，原版逻辑一个字节都不改。</p>
 *
 * <p>刻意<b>不</b> shadow 原版 {@code providers} / {@code spriteSets} 内部表——它们在 Fabric 是
 * {@code Int2ObjectMap}、NeoForge 是 {@code Map<RL,...>}、1.21.11+ 更是整体挪进 {@code ParticleResources}，
 * 跨 loader/版本严重分化，无法统一移植。改成完全自托管后，只 shadow 一个各版本都在的
 * {@code protected ClientLevel level} 字段递给 provider。</p>
 *
 * <p>{@link ParticleEngineAtlasAccess}：粒子贴图从 {@code ParticleEngine} <em>自持</em>的图集现取
 * （见接口注释）。{@code Minecraft.getTextureAtlas} 在 1.20.1/1.21.1 只查 {@code ModelManager} 的 AtlasSet，
 * 里面没有粒子图集，硬取会 NPE。经核对官方映射里图集字段名/类型在 ≤1.21.1 各版本统一是
 * {@code TextureAtlas textureAtlas}；≥1.21.11 图集整体移出引擎、改出 {@code AtlasManager} 统一管理，其
 * {@code getAtlasOrThrow} 的键是短名 definitionLocation（无法稳定拼），而那图集又以 {@code textureId}
 * （{@code textures/atlas/particles.png}）注册进 {@code TextureManager}，故那侧改走 {@code getTextureManager().getTexture(textureId)}。</p>
 */
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin implements ParticleEngineAtlasAccess {
	// makeParticle 从这里取 level 递给 provider；非 final（setLevel 会重新赋值），故不加 @Final。
	@Shadow
	protected ClientLevel level;

	// 粒子图集（引擎自持）。≤1.21.1 各版本字段名/类型统一；≥1.21.11 无此字段，不 shadow。
	//? if < 1.21.11
	//@Final @Shadow private TextureAtlas textureAtlas;

	// handler 签名 = 目标方法形参逐个同类型 + CallbackInfoReturnable；泛型 T 取擦除后的 ParticleOptions 即可。
	@SuppressWarnings("unchecked")
	@Inject(method = "makeParticle", at = @At("HEAD"), cancellable = true)
	private <T extends ParticleOptions> void fpp$makeParticle(T type, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
		ParticleProvider<T> provider = (ParticleProvider<T>) FppParticleRegistry.providerOf(type.getType());
		if (provider == null)
			return; // 不是我们的粒子：交回原版，不作任何改动
		// provider.createParticle 的参数集在 1.21.11+ 多一个 RandomSource（原版 makeParticle 内部本就临时造一个），此处对齐。
		//? if >= 1.21.11 {
		cir.setReturnValue(provider.createParticle(type, this.level, x, y, z, xSpeed, ySpeed, zSpeed, RandomSource.create()));
		//? } else {
		/*cir.setReturnValue(provider.createParticle(type, this.level, x, y, z, xSpeed, ySpeed, zSpeed));
		*///? }
	}

	// 从引擎自持的粒子图集现取 sprite。缺失时返回 missing-texture（不崩溃）。
	//? if >= 1.21.11 {
	@Override
	public TextureAtlasSprite fpp$particleSprite(Identifier id) {
		// ≥​1.21.11 粒子图集改由 AtlasManager 统一管理，getAtlasOrThrow 的键是 definitionLocation（短名，
		// 各版本不同且无法稳定拼出）；但同一图集又以 textureId（= textures/atlas/particles.png，常量稳定）
		// 注册进 TextureManager（见 AtlasManager 构造器 textureManager.register(config.textureId, atlas)），
		// 故走 getTextureManager().getTexture(textureId) 取回它最稳妥（旧长 id 对 getAtlasOrThrow 会抛 Invalid atlas id）。
		return ((TextureAtlas) Minecraft.getInstance().getTextureManager()
				.getTexture(VersionUtil.getId("minecraft", "textures/atlas/particles.png")))
				.getSprite(id);
	}
	//? } else {
	/*@Override
	public TextureAtlasSprite fpp$particleSprite(ResourceLocation id) {
		return this.textureAtlas.getSprite(id);
	}
	*///? }
}
