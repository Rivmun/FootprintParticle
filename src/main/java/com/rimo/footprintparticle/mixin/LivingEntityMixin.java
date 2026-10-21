package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.VersionUtil;
import com.rimo.footprintparticle.config.WorkMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if 1.16.5 {
/*import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
*///? }
//? if < 1.21.11
//import com.rimo.footprintparticle.PlatformUtil;
//? if < 1.21.1 && > 1.16.5
//import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.rimo.footprintparticle.Client.CONFIG;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
		fpp$lastX = this.getX();
		fpp$lastY = this.getY();
		fpp$lastZ = this.getZ();
	}

	@Unique private int fpp$timer = 0;
	@Unique private boolean fpp$wasFalling;
	@Unique private int fpp$wetTimer = CONFIG.getWetDuration() * 20;
	@Unique private double fpp$lastX, fpp$lastZ, fpp$lastY;
	@Unique private static boolean fpp$noPehkui, fpp$noRandomMobsizes;

	@Inject(method = "tick", at = @At("TAIL"))
	public void fpp$tick(CallbackInfo ci) {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		boolean moved = Math.abs(x - this.fpp$lastX) + Math.abs(z - this.fpp$lastZ) > 1.0E-4;
		// Remote player using lerp to calc its pos that cannot get onGround() in accuracy, so we detect whether the dy is flipped.
		double dy = y - this.fpp$lastY;
		// -0.25 is the velocity Y after player freefall around 0.5 blocks. Equivalent to we've filtered out the falling distance lower than the 0.5 blocks.
		boolean falling = dy < -0.25;
		boolean landed = fpp$wasFalling && !falling;
		this.fpp$lastX = x;
		this.fpp$lastY = y;
		this.fpp$lastZ = z;
		fpp$wasFalling = falling;

		boolean generated = false;
		if (!this.isShiftKeyDown() && !this.isUnderWater()) {
			//~ if < 1.20.1 'this.onGround()' -> 'this.onGround'
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
		if (this.isSwimming() && fpp$isEnable(CONFIG.getSwimPopLevel())) {
			float range = fpp$getEntityScale((LivingEntity) (Object) this);
			//~ if < 1.20.1 'this.level()' -> 'this.level'
			this.level().addParticle(
					ParticleTypes.BUBBLE,
					this.getX() + (Math.random() - 0.5f) * range,
					this.getY() + (Math.random() - 0.5f) * range,
					this.getZ() + (Math.random() - 0.5f) * range,
					0,
					Math.random() / 10f,
					0
			);
		}
	}

	@Unique
	public boolean fpp$footprintGenerator(boolean landedOrJump) {
		if (!fpp$isEnable(CONFIG.getEnableMod()))
			return false;
		String id = EntityType.getKey(this.getType()).toString();
		if (CONFIG.getExcludedMobSet().contains(id))
			return false;
		if (!CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return false;
		//~ if < 1.20.1 'this.level()' -> 'this.level'
		Level level = this.level();
		double px = this.getX();
		double py = this.getY();
		double pz = this.getZ();
		float yawRad = (float) Math.toRadians(this.getRotationVector().y);
		float dx = - Mth.sin(yawRad);  // horizontal facing vector.
		float dz =   Mth.cos(yawRad);
		LivingEntity entity = (LivingEntity) (Object) this;
		float scale = fpp$getEntityScale(id, entity);
		boolean isSprinting = this.isSprinting();

		// Set Interval
		fpp$timer = isSprinting ? (int) (CONFIG.getSecPerPrint() * 13.33f) : (int) (CONFIG.getSecPerPrint() * 20);
		Float interval = CONFIG.getMobIntervalMap().get(id);
		if (interval != null)
			fpp$timer *= interval;

		// Fix pos. 2 steps:
		// 1.Horizontal Offset
		// 1.1 Front and back
		double fbOffset = 0.075f;  // default f/b offset for all entity to decrease repetition.
		Map<String, Float> horseMap = CONFIG.getHorseLikeMobsMap();
		if (horseMap.containsKey(id)) {
			Float front = horseMap.get(id);
			fbOffset = front != null ? front : 0.75f;  // 0.75F if value not found (matching the vanilla horse)
			Entity passenger = this.getControllingPassenger();  // speed up the generation if ridden by player.
			//~ if 1.16.5 'passenger != null && passenger.isAlwaysTicking()' -> 'passenger instanceof Player'
			fpp$timer *= (passenger != null && passenger.isAlwaysTicking() ? 0.5f : 1.33f);
		}
		double fB = fbOffset * scale * (Math.random() > 0.5f ? 1 : -1);  // random a side.
		// 1.2 Left and right
		double lrOffset = 0.15f;  // default l/r offset for all entity to decrease repetition. (matching vanilla player)
		Map<String, Float> spiderMap = CONFIG.getSpiderLikeMobsMap();
		if (spiderMap.containsKey(id)) {
			Float lateral = spiderMap.get(id);
			lrOffset = lateral != null ? lateral : 0.9f;  // 0.9F if value not found (matching the vanilla spider)
		}
		double lR = lrOffset * scale * (Math.random() > 0.5f ? 1 : -1);
		// 1.3 Apply: f/b follows the facing vector (sin, cos), l/r follows its right normal (-cos, sin).
		px += fB * dx - lR * dz;
		pz += fB * dz + lR * dx;

		// 2.Vertical Offset
		int colX = Mth.floor(px), colZ = Mth.floor(pz), feetY = Mth.floor(py);
		boolean canGen = false;
		BlockState blockState = null;
		String blockName = null;
		List<String> blockTagNames = null;

		for (int i = 0; i < 2; i++) {  // 2 loop, first is feetBlock, second is belowBlock.
			BlockPos pos = new BlockPos(colX, feetY - i, colZ);
			blockState = level.getBlockState(pos);
			VoxelShape blockShape = blockState.getCollisionShape(level, pos);
			blockName = VersionUtil.getBlockName(blockState);
			blockTagNames = VersionUtil.getBlockTagNames(blockState);
			boolean isInBlackList = fpp$isInSet(blockName, blockTagNames, CONFIG.getExcludedBlockSet());
			double tempY = Double.NEGATIVE_INFINITY;

			// Hope you can understand what you have written here in the future :P
			if (fpp$isInSet(blockName, blockTagNames, CONFIG.getApplyBlockSet())) {  // Highest priority.
				tempY = blockShape.isEmpty() ?
						feetY - i :
						Math.min(pos.getY() + blockShape.max(Direction.Axis.Y), py);  // strict print pos never higher than feet.
			} else {
				boolean isFullInHorizontal = blockShape.max(Direction.Axis.X) - blockShape.min(Direction.Axis.X) > 0.999
						&& blockShape.max(Direction.Axis.Z) - blockShape.min(Direction.Axis.Z) > 0.999;
				if (fpp$isFragile(blockState) && ! isInBlackList) {  // no in whiteList but passed...
					if (isFullInHorizontal   // if block's horizontal direction is full (considering has a shape)...
							&& (i == 0 || blockState.canOcclude())) {  // belowBlock must occlude...
						tempY = Math.min(pos.getY() + blockShape.max(Direction.Axis.Y), py);  // strict print pos never higher than feet.
					} else if (! blockState.isAir() && blockState.canOcclude()) {  // if no shape (horizontal non-full), isn't air, but occlusion...(1 layer of snowLayer I mean.)
						tempY = feetY - i;
					}  // no shape, air or facilitate...
				} else if (! isInBlackList && ! isFullInHorizontal && i == 0) {  // too hard but non-full...
					continue;  // check its below...(allow to gen in same pos with horizontal non-full blocks, if its below is passed)
				} else if (isInBlackList) {  // in blackList...
					break;  // jump, only try to gen water print & splash.
				}  // else continue;
			}

			if (tempY != Double.NEGATIVE_INFINITY) {
				if (tempY <= py - CONFIG.getGenHeightGap() / (landedOrJump ? 1F : 4F)) {  // Limited dist from feet to print. walking must closer than jumping.
					fpp$timer = 0;  // reset timer because nothing generated.
					return false;  // reject if entity floating in air (at edge of block).
				} else {
					py = tempY;
					canGen = true;
					break;
				}
			}
		}
		if (! canGen)
			py = feetY;  // provide current feet Y to waterPrint if footPrint cannot to gen.
		py += 0.01F;  // prevent z-fight with terrain.

		// Generate
		int wetLimit = CONFIG.getWetDuration() * 20;
		if (canGen) {
			// Fix height by blocks if in...(extra vertical offset)
			Map<String, Float> heightMap = CONFIG.getBlockHeightMap();
			Float height = heightMap.get(blockName);
			if (height != null) {
				py += height;
			} else {
				py += blockTagNames.stream().map(heightMap::get).filter(Objects::nonNull).findFirst().orElse(0f);
			}

			// footprint
			level.addParticle(Client.FOOTPRINT.setEntity(entity), px, py, pz, dx, scale, dz);
			// Snow Dust
			if (blockState.is(Blocks.SNOW) && fpp$isEnable(CONFIG.getSnowDustLevel())) {
				int count = isSprinting ? 4 : 2;
				int range = isSprinting ? 3 : 10;
				while (--count >= 0) {
					level.addParticle(Client.SNOWDUST.setSize(scale), px, py, pz,
							(Math.random() - 0.5f) / range,
							0,
							(Math.random() - 0.5f) / range
					);
				}
			}
		} else if (fpp$wetTimer <= wetLimit) {
			// waterprint (gen when footprint not gen)
			level.addParticle(Client.WATERMARK.setStartAlpha(fpp$wetTimer).setEntity(entity),   // push timer to calc alpha
					px, py, pz, dx, scale, dz);
		}
		// water splash (gen whatever print gen)
		if (fpp$wetTimer <= wetLimit && fpp$isEnable(CONFIG.getWaterSplashLevel())) {
			int count = (int)((isSprinting ? 18 : 10) * Math.max((0.7f - (float) fpp$wetTimer / (wetLimit)), 0));
			int range = isSprinting ? 3 : 6;
			//~ if 1.16.5 '.horizontalDistance' -> '.length'
			double velocity = this.getDeltaMovement().horizontalDistance();
			while (--count > 0) {
				level.addParticle(
						Client.WATERSPLASH,
						px - 0.25f * scale + Math.random() / 4,
						py,
						pz - 0.25f * scale + Math.random() / 4,
						(Math.random() - 0.5f) / range,
						0.02f + Math.random() * velocity,
						(Math.random() - 0.5f) / range
				);
			}
		}

		return canGen;
	}

	// Hardness Filter. See on https://minecraft.fandom.com/wiki/Breaking#Blocks_by_hardness
	@Unique
	private boolean fpp$isFragile(BlockState blockState) {
		//~ if 1.16.5 '.defaultDestroyTime' -> '.getExplosionResistance'
		float hardness = blockState.getBlock().defaultDestroyTime();
		float hardnessGate = CONFIG.getHardnessGate();
		return hardnessGate > 0 && hardness >= 0 && hardness < hardnessGate;
	}

	@Unique
	private boolean fpp$isInSet(String blockName, List<String> blockTagNames, Set<String> set) {
		if (set.isEmpty())
			return false;
		return set.contains(blockName) || blockTagNames.stream().anyMatch(set::contains);
	}

	@Unique
	private boolean fpp$isEnable(WorkMode workMode) {
		//~ if 1.16.5 'this.isAlwaysTicking()' -> '(((LivingEntity) (Object) this) instanceof Player)'
		return workMode == WorkMode.ALL || (workMode == WorkMode.PLAYER_ONLY && this.isAlwaysTicking());
	}

	@Unique
	private static float fpp$getEntityScale(LivingEntity entity) {
		return fpp$getEntityScale(EntityType.getKey(entity.getType()).toString(), entity);
	}

	@Unique
	private static float fpp$getEntityScale(String Id, LivingEntity entity) {
		float scale = 1f;

		Float configured = CONFIG.getSizePerMobMap().get(Id);
		if (configured != null)
			scale *= configured;

		//? if < 1.21.11 {
		/*if (! fpp$noPehkui && PlatformUtil.PLATFORM.isModLoaded("pehkui")) {
			try {
				scale *= virtuoel.pehkui.api.ScaleTypes.BASE.getScaleData(entity).getScale();
			} catch (Exception | LinkageError e) {
				fpp$noPehkui = true;
			}
		}
		*///? }
		// seems randommobsizes do not have its own scaling since vanilla makes one on 1.21+
		//? if < 1.21.1 && > 1.16.5 {
		/*if (! fpp$noRandomMobsizes && PlatformUtil.PLATFORM.isModLoaded("random_mob_sizes") && entity instanceof Mob) {
			try {
				//? if ! 1.19.2 {
				scale *= ((com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon) entity).getMobScaling$RandomMobSizes();
				//? } else {
				/^scale *= ((com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon) entity).getMobScaling();  // randommobsizes version < 2.0
				^///? }
			} catch (Exception | LinkageError e) {
				fpp$noRandomMobsizes = true;
			}
		}
		*///? }

		if (entity.isBaby())
			scale *= 0.66f;
		scale *= entity.getScale();
		return scale;
	}
}
