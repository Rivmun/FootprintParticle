//? if forge {
/*package com.rimo.footprintparticle.loaders.forge.mixin;

import com.rimo.footprintparticle.loaders.forge.ForgeParticleRegistry;
import com.rimo.footprintparticle.loaders.forge.ParticleEngineLocal;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if 1.16.5
//import java.lang.reflect.Constructor;
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
 *       可见性与构造方式随版本而异：1.18.2+ 它是 static 嵌套类（有无参构造器），可直接 {@code new}；
 *       1.16.5 它是非静态内部类（带 {@code final ParticleEngine} 外部引用字段、构造器还需一个合成
 *       marker 参数），Java 语法上写不出构造表达式（编译器把 {@code new ParticleEngine.MutableSpriteSet()}
 *       当成「限定式 new」，要求 ParticleEngine 是本类的封闭类），故只能反射构造，见
 *       {@link #fpp$newSpriteSet()}。
 *       类的可见性由各版本选用的 access widener（转成 Forge AT）放开：1.18.2+ 用共用的
 *       {@code footprintparticle.accesswidener}（class + {@code <init>()V}）；1.16.5 不存在 {@code ()V}
 *       构造器，改用只含 class 条目的 {@code footprintparticle-1.16.5.accesswidener}，
 *       private 构造器交给反射的 {@code setAccessible} 绕过。</li>
 *   <li>{@link #fpp$makeLocalParticle} 在 {@code makeParticle} 的 {@code HEAD} 上自己走完一次
 *       provider 解析：我们的粒子类型不在原版 {@code Registries.PARTICLE_TYPE} 里（在
 *       {@link ForgeParticleRegistry} 的私有表里），原版那句
 *       {@code providers.get(Registries.PARTICLE_TYPE.getKey(type.getType()))} 必定拿到 null key、查不到
 *       provider。于是这里先查私有表：命中就用我们自己注入 {@code providers} 的 provider 造出粒子并
 *       {@code setReturnValue} 取消原方法；不命中（所有非本模组的粒子）直接 {@code return}，原版逻辑
 *       一个字节都不被改动。
 *       刻意只用核心 Mixin 的 {@code @Inject}，不用 {@code @Redirect}，也不用 MixinExtras：
 *       <ul>
 *         <li>{@code @Redirect} 是【整条替换】目标 invoke 指令，同一条指令只允许一个替换者，别的 mod
 *             再注入同一处就是注入冲突。</li>
 *         <li>MixinExtras 在 Forge 端需自己打包进模组，模组体积增大数倍，且 {@code @WrapOperation} 和
 *              {@code @ModifyExpressionValue} 兼容性也不是万能的，需 Mixin 本体支持。</li>
 *         <li>{@code @Inject(HEAD, cancellable)} 无独占性：多个 mod 往同一方法 HEAD 注入本来就允许共存，
 *             对不属于我们的粒子又是完全 no-op，冲突面最小。</li>
 *       </ul></li>
 * </ol>
 *
 * <p>仅在 Forge 侧生效：本类位于 {@code loaders/forge} 包，被 Stonecutter 的 {@code //? if forge} 排除在
 * Fabric/NeoForge 之外；对应的 {@code footprintparticle-forge.mixins.json} 也只在 mods.toml 里声明。
 * {@code makeParticle} / {@code providers} / {@code spriteSets} / {@code level} / {@code MutableSpriteSet}
 * 为 1.20.1 (Mojang 映射) 名称。四个 forge 目标（1.16.5 / 1.18.2 / 1.19.2 / 1.20.1）里
 * {@code makeParticle} 均为 {@code private <T extends ParticleOptions> Particle makeParticle(T, double×6)}、
 * 字段均为 {@code protected ClientLevel level}，且方法体逐条指令同形，故无需任何版本变体。</p>
 ^/
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin implements ParticleEngineLocal {

	@Shadow @Final
	private Map<ResourceLocation, ParticleProvider<?>> providers;

	@Shadow @Final
	private Map<ResourceLocation, ParticleEngine.MutableSpriteSet> spriteSets;

	// makeParticle 原版就是从这里取 level 递给 provider；非 final（setLevel 会重新赋值），故不加 @Final。
	@Shadow
	protected ClientLevel level;

	@Override
	public <O extends ParticleOptions> void fpp$registerLocal(
			ResourceLocation id,
			Function<ParticleEngine.MutableSpriteSet, ? extends ParticleProvider<O>> providerFactory) {
		ParticleEngine.MutableSpriteSet spriteSet = this.fpp$newSpriteSet();
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

	// 自己完成一次 provider 解析，所以只靠 @Inject(HEAD)，不碰原方法体里的任何一条指令。
	// 形状四版全同（providers.get(Registries.PARTICLE_TYPE.getKey(type.getType())) → createParticle(type, level, D×6)）；
	// handler 签名 = 目标方法形参逐个同类型 + CallbackInfoReturnable，泛型 T 取擦除后的 ParticleOptions 即可。
	@SuppressWarnings("unchecked")
	@Inject(method = "makeParticle", at = @At("HEAD"), cancellable = true)
	private <T extends ParticleOptions> void fpp$makeLocalParticle(T type, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
		ResourceLocation id = ForgeParticleRegistry.keyOf(type.getType());
		if (id == null)
			return; // 不是我们的粒子：交回原版，不作任何改动
		ParticleProvider<T> provider = (ParticleProvider<T>) this.providers.get(id);
		if (provider == null)
			return; // 我们自己的 id 在引擎里没 provider（未被调用过 fpp$registerLocal）：同样不介入
		cir.setReturnValue(provider.createParticle(type, this.level, x, y, z, xSpeed, ySpeed, zSpeed));
	}

	// 1.16.5 的 MutableSpriteSet 是非静态内部类，语法上无法构造（需 ParticleEngine 实例作为外壳），只能反射：
	// 该类有两个声明构造器——私有的 MutableSpriteSet(ParticleEngine) 与编译器合成的桥
	// MutableSpriteSet(ParticleEngine, ParticleEngine$1)，取参数最多的那个（合成桥），marker 位传 null 即可。
	// 构造器只在首次调用时解析一次，之后直接复用。
	//? if 1.16.5 {
	/^@Unique
	private static Constructor<?> fpp$spriteSetCtor;

	@Unique
	private ParticleEngine.MutableSpriteSet fpp$newSpriteSet() {
		Constructor<?> ctor = fpp$spriteSetCtor;
		if (ctor == null) {
			Constructor<?>[] declared = ParticleEngine.MutableSpriteSet.class.getDeclaredConstructors();
			for (Constructor<?> candidate : declared)
				if (ctor == null || candidate.getParameterCount() > ctor.getParameterCount())
					ctor = candidate;
			ctor.setAccessible(true);
			fpp$spriteSetCtor = ctor;
		}
		try {
			ParticleEngine outer = (ParticleEngine) (Object) this;
			Object[] args = ctor.getParameterCount() == 1
					? new Object[]{outer}
					: new Object[]{outer, null};
			return (ParticleEngine.MutableSpriteSet) ctor.newInstance(args);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Failed to create ParticleEngine.MutableSpriteSet", e);
		}
	}
	^///? } else {
	private ParticleEngine.MutableSpriteSet fpp$newSpriteSet() {
		return new ParticleEngine.MutableSpriteSet();
	}
	//? }
}
*///? }
