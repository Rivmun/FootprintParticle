//? if forge {
/*package com.rimo.footprintparticle.loaders.forge.mixin;

import com.rimo.footprintparticle.loaders.forge.ForgeParticleRegistry;
import com.rimo.footprintparticle.loaders.forge.ParticleEngineLocal;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.function.Function;

/^*
 * 劫持原版 {@code ParticleEngine}，为纯客户端粒子打通"私有注册表 + 直接注入 provider"的生成路径。
 *
 * <p>两处协同：</p>
 * <ol>
 *   <li>{@link #fpp$registerLocal} 通过 {@link ParticleEngineLocal} 接口被混入到 ParticleEngine，
 *       把 provider 与一个空的 {@link ParticleEngine.MutableSpriteSet} 按 RL 塞进内部两张
 *       {@code Map<ResourceLocation, ...>}（{@code providers} / {@code spriteSets}）。
 *       {@code MutableSpriteSet} 及其构造器由 Forge 内置 access transformer 置为 public，故可直接 new。</li>
 *   <li>{@link #fpp$resolveParticleTypeId} 用核心 Mixin 的 {@code @Redirect} 拦截
 *       {@code makeParticle} 里对 {@code Registries.PARTICLE_TYPE.getKey(type)} 的调用：我们的粒子类型
 *       不在原版表里（在私有表里），原版返回 null，这里回退到 {@link ForgeParticleRegistry#keyOf} 取 RL，
 *       从而使后续 {@code providers.get(rl)} 命中我们注入的 provider。此步替代了 Visuality 用 MixinExtras
 *       {@code @ModifyExpressionValue} 的做法，不需要引入 MixinExtras 依赖。</li>
 * </ol>
 *
 * <p>仅在 Forge 侧生效：本类位于 {@code loaders/forge} 包，被 Stonecutter 的 {@code //? if forge} 排除在
 * Fabric/NeoForge 之外；对应的 {@code footprintparticle-forge.mixins.json} 也只在 mods.toml 里声明。
 * {@code makeParticle} / {@code providers} / {@code spriteSets} / {@code MutableSpriteSet} 为 1.20.1
 * (Mojang 映射) 名称；重新启用更旧 forge 版本时需按版本加 {@code //~} 变体。</p>
 ^/
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin implements ParticleEngineLocal {

	@Shadow @Final
	private Map<ResourceLocation, ParticleProvider<?>> providers;

	@Shadow @Final
	private Map<ResourceLocation, ParticleEngine.MutableSpriteSet> spriteSets;

	@Override
	public <O extends ParticleOptions> void fpp$registerLocal(
			ResourceLocation id,
			Function<ParticleEngine.MutableSpriteSet, ? extends ParticleProvider<O>> providerFactory) {
		ParticleEngine.MutableSpriteSet spriteSet = new ParticleEngine.MutableSpriteSet();
		// >1.19.2：放入原版 spriteSets，粒子图集烘焙会由 vanilla bake 回填 sprites（1.20.1 即靠此显示）。
		// ≤1.19.2：vanilla bake 无法为未注册类型解析贴图，且 ParticleEngine.reloadSprites 会在我们
		// TextureStitchEvent.Post 回填之后又对 spriteSets 里的 set 调 bake 把 sprites 清空；故这里
		// 【不】放入 spriteSets，改由 ForgeParticleAtlas 在 Post 手动回填并持有该 set。
		//? if > 1.19.2 {
		this.spriteSets.put(id, spriteSet);
		//? }
		ForgeParticleRegistry.putSpriteSet(id, spriteSet);
		this.providers.put(id, providerFactory.apply(spriteSet));
	}

	@Redirect(
			method = "makeParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/core/Registry;getKey(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;"
			)
	)
	private ResourceLocation fpp$resolveParticleTypeId(Registry registry, Object type) {
		ResourceLocation id = registry.getKey(type);
		return id != null ? id : ForgeParticleRegistry.keyOf((ParticleType<?>) type);
	}
}
*///? }
