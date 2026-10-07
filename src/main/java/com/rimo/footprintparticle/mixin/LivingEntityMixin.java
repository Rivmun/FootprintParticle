package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.Util;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
//~ if < 1.21.11 'Identifier' -> 'ResourceLocation'
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.rimo.footprintparticle.Client.CONFIG;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	//~ if < 1.21.11 'Identifier' -> 'ResourceLocation'
	@Unique private final ResourceKey<Block> fpp$AIR = ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("air"));
	@Unique private int fpp$timer = 0;
	@Unique private boolean fpp$wasFalling;
	@Unique private int fpp$wetTimer = CONFIG.getWetDuration() * 20;
	@Unique private double fpp$lastX, fpp$lastZ, fpp$lastY;  // 上一 tick 位置：水平位移判定移动（远程玩家 getDeltaMovement() 恒≈ 0），竖直位移判定跳跃/落地（远程玩家 onGround() 连跳时不可靠）。

	@Inject(method = "tick", at = @At("TAIL"))
	public void fpp$tick(CallbackInfo ci) {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		boolean moved = Math.abs(x - this.fpp$lastX) + Math.abs(z - this.fpp$lastZ) > 1.0E-4;
		// 竖直位移：远程玩家连跳时 onGround() 在客户端始终采不到那一两 tick 的贴地瞬间，
		// 但其插值后的 Y 会随每次起跳/落地明显地升-降波动，故用“下落→停”的竖直速度符号翻转判定触地。
		// 阈值 -0.25：按 MC 落体递推 s_k=(s_{k-1}+0.08)*0.98，自由下落约 0.5 格时本 tick dy≈ 0.25。
		// 因此只把下落距离≥～0.5 格计为 falling，排除下台阶/雪层/半砖回落等小降幅误入落地分支（应走带 feetFrac 安全闸的走路分支），
		// 而普通跳跃/远程连跳滞空超过 1 格、落地前速度远超 0.25，仍稳定触发 landed。
		double dy = y - this.fpp$lastY;
		this.fpp$lastX = x;
		this.fpp$lastY = y;
		this.fpp$lastZ = z;
		boolean falling = dy < -0.25;
		boolean landed = fpp$wasFalling && !falling;
		fpp$wasFalling = falling;

		boolean generated = false;
		if (!this.isShiftKeyDown() && !this.isUnderWater()) {
			if (landed || (fpp$timer <= 0 && moved && this.onGround()))
				generated = this.fpp$footprintGenerator(landed);
		}
		if (!generated && fpp$timer > 0) {
			fpp$timer--;
		}

		if (this.isInWaterOrRain()) {
			fpp$wetTimer = 0;
		} else if (fpp$wetTimer <= CONFIG.getWetDuration() * 20) {
			fpp$wetTimer++;
		}

		// Swim Pop
		if (this.isSwimming() && (
				CONFIG.getSwimPopLevel() == 2 || (
						CONFIG.getSwimPopLevel() == 1 && this.isAlwaysTicking()
				)
		)) {
			float range = Util.getEntityScale((LivingEntity) (Object) this);
			this.level().addParticle(
					ParticleTypes.BUBBLE,
					this.getX() + Math.random() - 0.5f * range,
					this.getY() + Math.random() - 0.5f * range,
					this.getZ() + Math.random() - 0.5f * range,
					0,
					Math.random() / 10f,
					0
			);
		}
	}

	@Unique
	public boolean fpp$footprintGenerator(boolean landedOrJump) {
		if (CONFIG.isEnable() == 0 ||
				(CONFIG.isEnable() == 1 && !this.isAlwaysTicking()))
			return false;
		// 实体 ID 只需算一次，后面的排除名单/间隔/偏移/尺寸全部直接查 Client 里由配置预建的索引。
		String id = EntityType.getKey(this.getType()).toString();
		if (Client.EXCLUDED_MOBS.contains(id))
			return false;
		if (!CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return false;

		// Set Interval
		fpp$timer = this.isSprinting() ? (int) (CONFIG.getSecPerPrint() * 13.33f) : (int) (CONFIG.getSecPerPrint() * 20);
		Float interval = Client.MOB_INTERVAL.get(id);
		if (interval != null)
			fpp$timer *= interval;

		// Fix pos...
		var px = this.getX();
		var py = this.getY();
		var pz = this.getZ();
		var scale = Util.getEntityScale((LivingEntity) (Object) this);

		// Horizontal Offset
		// Front and back
		var side = Math.random() > 0.5f ? 1 : -1;
		var hOffset = 0.0625f;
		// 四足：命中即换成配置的前后偏移（索引里已把缺多数值的行补成 0.75），并按骑乘情况调整间隔。
		Float front = Client.HORSE_LIKE_MOBS.get(id);
		if (front != null) {
			hOffset = front;
			fpp$timer = (int) (this.getControllingPassenger() != null ?
					this.getControllingPassenger().isAlwaysTicking() ?
							fpp$timer * 0.5f :
							fpp$timer * 1.33f :
						fpp$timer * 1.33f
			);
		}
		hOffset *= scale;
		px = px - hOffset * side * Mth.sin((float) Math.toRadians(this.getRotationVector().y));
		pz = pz + hOffset * side * Mth.cos((float) Math.toRadians(this.getRotationVector().y));
		// Left and right
		side = Math.random() > 0.5f ? 1 : -1;
		hOffset = 0.125f;
		// 多足：同上，缺多数值时索引里已补成 0.9。
		Float lateral = Client.SPIDER_LIKE_MOBS.get(id);
		if (lateral != null)
			hOffset = lateral;
		hOffset *= scale;
		px = px - hOffset * side * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90));
		pz = pz + hOffset * side * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90));

		// 只考察“脚所在格”与“脚下一格”这两格，不再向下深扫（避免落在方块边缘时把脚印隔空贴到下方格外的方块上）。
		// 三分支：脚格有碰撞→贴脚格顶面；脚格无碰撞但非空气且脚踩其顶(feetFrac≥0.05，如雪层)→以精确脚Y为面并归属该方块；否则(脚格空气或贴地装饰)用脚下一格作支撑。
		// 各分支都把便宜的配置剔除(isPrintCanGen)放在复杂碰撞体度量(canOcclude/isShapeFullBlock/横向跨度)之前短路。
		int colX = Mth.floor(px), colZ = Mth.floor(pz), feetY = Mth.floor(py);
		BlockPos pos;
		boolean canGen;

		var feetPos = new BlockPos(colX, feetY, colZ);
		var feetState = this.level().getBlockState(feetPos);
		var feetShape = feetState.getCollisionShape(this.level(), feetPos);
		if (!feetShape.isEmpty()) {
			// 脚格有碰撞箱：直接贴脚格顶面。
			pos = feetPos;
			py = feetY + feetShape.max(Direction.Axis.Y) + 0.01f + CONFIG.getPrintHeight();
			canGen = fpp$isPrintCanGen(pos);
		} else if (!feetState.isAir() && feetState.canOcclude()) {
			// 脚格无碰撞箱但非空气、且脚踩在其顶面(feetFrac≥～0.05)＝实心却碰撞极薄的扁平方块(雪层等)：脚正压在它顶面。
			// 以精确脚Y作面(=feetY+feetFrac，已含雪实际厚度，勿再叠加配置雪偏移否则双重抬高)，并把 pos 归到该方块，
			// 使 isPrintCanGen / 高度修正 / 雪尘都按雪层而非下方泥土触发。feetFrac<0.05 不进来：草/花/树苗等贴地装饰脚 Y 为整数(踩在下方块顶)，落到 else 分支归属下方支撑方块。
			pos = feetPos;
			py = feetY + 0.01f + CONFIG.getPrintHeight();
			canGen = fpp$isPrintCanGen(pos);
		} else {
			// 脚格是空气，才用脚下一格作支撑：走路须贴地整数Y(feetFrac≈0，防止走到非完整方块边缘时脚悬空在旁边空气、误判到下方一格而隔空)，且须完整不透明方块；
			// 跳跃/落地只要有碰撞箱即可(兼容半砖/雪片)，但排除两类：①“完整方块却不遮光”的透明方块(玻璃/树叶)；②横向铺不满整格的窄碰撞体(栏杆/玻璃板/墙)。
			// 草/花这类贴地装饰(feetFrac≈0)也走这里：脚压在下方块顶、其上方一格为空气，故归属下方支撑方块，行为不变。
			pos = new BlockPos(colX, feetY - 1, colZ);
			var belowState = this.level().getBlockState(pos);
			var belowShape = belowState.getCollisionShape(this.level(), pos);
			double feetFrac = py - feetY;   // 脚在其格内的离地高度：走路贴地时应≈0（Y 为整数），非零说明脚悬空在非完整方块的高度上。
			py = feetY - 1 + (belowShape.isEmpty() ? 0.0 : belowShape.max(Direction.Axis.Y)) + 0.01f + CONFIG.getPrintHeight();
			canGen = !belowShape.isEmpty() && fpp$isPrintCanGen(pos)
					&& (landedOrJump
							? (belowShape.max(Direction.Axis.X) - belowShape.min(Direction.Axis.X) > 0.999
									&& belowShape.max(Direction.Axis.Z) - belowShape.min(Direction.Axis.Z) > 0.999
									&& !(Block.isShapeFullBlock(belowShape) && !belowState.canOcclude()))
							: (feetFrac < 0.05 && belowState.canOcclude() && Block.isShapeFullBlock(belowShape)));
		}

		if (canGen) {
			// Fix height by blocks if in...（保留原有按方块/标签修正 y 的配置逻辑，现作用于实际站立的支撑方块）
			try {
				var block = this.level().getBlockState(pos);
				//~ if < 26.1 '.typeHolder()' -> '.getBlockHolder()'
				//~ if < 1.21.11 '.identifier()' -> '.location()'
				var blockId = block.typeHolder().unwrapKey().orElse(fpp$AIR).identifier().toString();
				// 高度修正走索引：方块 ID 命中就只用它；否则叠加该方块所属标签命中的偏移。
				// 与旧写法的细微差别：旧版在配置里同时写了 ID 和标签时按行序可能叠加，现在固定为“ID 优先、标签可叠加”。
				Float height = Client.BLOCK_HEIGHT.get(blockId);
				if (height != null) {
					py += height;
				} else {
					//~ if < 26.1 '.tags()' -> '.getTags()'
					for (TagKey<Block> tag : block.tags().toList()) {
						Float tagHeight = Client.BLOCK_HEIGHT_BY_TAG.get("#" + tag.location());
						if (tagHeight != null)
							py += tagHeight;
					}
				}

				// Snow Dust
				if (block.is(Blocks.SNOW) &&
						(CONFIG.getSnowDustLevel() == 2 ||
								(CONFIG.getSnowDustLevel() == 1 && this.isAlwaysTicking()))) {
					int i = this.isSprinting() ? 4 : 2;
					int v = this.isSprinting() ? 3 : 10;
					while (--i >= 0) {
						SnowDustParticleType snowdust = Client.SNOWDUST;
						this.level().addParticle(snowdust.setData(scale), px, py, pz,
								(Math.random() - 0.5f) / v,
								0,
								(Math.random() - 0.5f) / v
						);
					}
				}

			} catch (Exception e) {
				// Ignore...
			}
		}

		// Generate
		double dx, dz;      // get facing
		if (this.getDeltaMovement().horizontalDistance() == 0) {
			dx = -Mth.sin((float) Math.toRadians(this.getRotationVector().y));
			dz =  Mth.cos((float) Math.toRadians(this.getRotationVector().y));
		} else {
			dx = this.getDeltaMovement().x();
			dz = this.getDeltaMovement().z();
		}
		if (canGen) {       // footprint
			FootprintParticleType footprint = Client.FOOTPRINT;
			this.level().addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, dx, 0, dz);
		} else if (fpp$wetTimer <= CONFIG.getWetDuration() * 20) {        // waterprint (gen when footprint not gen)
			WatermarkParticleType watermark = Client.WATERMARK;
			var i = Math.random() > 0.5f ? 1 : -1;
			this.level().addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, dx * i, fpp$wetTimer, dz * i);		// push timer to calc alpha
		}
		// water splash (gen whatever print gen)
		if (fpp$wetTimer <= CONFIG.getWetDuration() * 20 &&
				(CONFIG.getWaterSplashLevel() == 2 ||
						(CONFIG.getWaterSplashLevel() == 1 && this.isAlwaysTicking()))) {
			float range = Util.getEntityScale((LivingEntity) (Object) this);
			int i = (int)((this.isSprinting() ? 18 : 10) * Math.max((0.7f - (float) fpp$wetTimer / (CONFIG.getWetDuration() * 20)), 0));
			int v = this.isSprinting() ? 3 : 6;
			while (--i > 0) {
				this.level().addParticle(
					Client.WATERSPLASH,
						px - 0.25f * range + Math.random() / 4,
						py,
						pz - 0.25f * range + Math.random() / 4,
						(Math.random() - 0.5f) / v,
						0.02f + Math.random() * this.getDeltaMovement().horizontalDistance(),
						(Math.random() - 0.5f) / v
				);
			}
		}

		return canGen;
	}

	//~ if < 26.1 '.tags()' -> '.getTags()' {
	//~ if < 26.1 '.typeHolder()' -> '.getBlockHolder()' {
	//~ if < 1.21.11 'identifier().' -> 'location().' {
	@Unique
	private boolean fpp$isPrintCanGen(BlockPos pos) {
		var block = this.level().getBlockState(pos);
		// 名单已从 List 换成 Set：contains 由 O(n) 降为 O(1)，匹配字符串与旧写法完全一致。
		var blockId = block.typeHolder().unwrapKey().orElse(fpp$AIR).identifier().toString();
		var canGen = Client.APPLY_BLOCKS.contains(blockId);
		if (!canGen) {
			for (TagKey<Block> tag : block.tags().toList()) {
				canGen = Client.APPLY_BLOCKS.contains("#" + tag.location());
				if (canGen)
					break;
			}
			if (!canGen) {
				// Hardness Filter. See on https://minecraft.fandom.com/wiki/Breaking#Blocks_by_hardness
				float hardness = block.getBlock().defaultDestroyTime();
				float hardnessGate = CONFIG.getHardnessGate();
				canGen = hardnessGate > 0 && hardness >= 0 && Mth.abs(hardness) < hardnessGate;
				if (canGen) {
					canGen = !Client.EXCLUDED_BLOCKS.contains(blockId);
					if (canGen) {
						for (TagKey<Block> tag : block.tags().toList()) {
							canGen = !Client.EXCLUDED_BLOCKS.contains("#" + tag.location());
							if (!canGen)
								break;
						}
					}
				}
			}
		}
		return canGen;
	}
	//~ }
	//~ }
	//~ }
}
