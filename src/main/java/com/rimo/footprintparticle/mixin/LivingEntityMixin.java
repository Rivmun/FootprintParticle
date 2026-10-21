package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.Util;
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

import java.util.Objects;
import java.util.Set;

import static com.rimo.footprintparticle.Client.CONFIG;

//~ if < 1.20.1 'this.level()' -> 'this.level' {
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
		if (!fpp$isEnable(CONFIG.getEnableMod()))
			return false;
		String id = EntityType.getKey(this.getType()).toString();
		if (CONFIG.getExcludedMobSet().contains(id))
			return false;
		if (!CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return false;

		// Set Interval
		fpp$timer = this.isSprinting() ? (int) (CONFIG.getSecPerPrint() * 13.33f) : (int) (CONFIG.getSecPerPrint() * 20);
		Float interval = CONFIG.getMobIntervalMap().get(id);
		if (interval != null)
			fpp$timer *= interval;

		// Fix pos. 2 steps:
		double px = this.getX();
		double py = this.getY();
		double pz = this.getZ();
		float scale = Util.getEntityScale((LivingEntity) (Object) this);

		// 1.Horizontal Offset
		// 1.1 Front and back
		int side = Math.random() > 0.5f ? 1 : -1;
		double hOffset = 0.075f;  // default f/b offset for all entity to decrease repetition.
		java.util.Map<String, Float> horseMap = CONFIG.getHorseLikeMobsMap();
		if (horseMap.containsKey(id)) {
			Float front = horseMap.get(id);
			hOffset = front != null ? front : 0.75f;  // 0.75F if value not found (matching the vanilla horse)
			fpp$timer = (int) (this.getControllingPassenger() != null ?
					//~ if 1.16.5 '.isAlwaysTicking()' -> ' instanceof Player'
					this.getControllingPassenger().isAlwaysTicking() ?
							fpp$timer * 0.5f :
							fpp$timer * 1.33f :
					fpp$timer * 1.33f
			);
		}
		hOffset *= scale;
		px -= hOffset * side * Mth.sin((float) Math.toRadians(this.getRotationVector().y));
		pz += hOffset * side * Mth.cos((float) Math.toRadians(this.getRotationVector().y));
		// 1.2 Left and right
		side = Math.random() > 0.5f ? 1 : -1;
		hOffset = 0.15f;  // default l/r offset for all entity to decrease repetition. (matching vanilla player)
		java.util.Map<String, Float> spiderMap = CONFIG.getSpiderLikeMobsMap();
		if (spiderMap.containsKey(id)) {
			Float lateral = spiderMap.get(id);
			hOffset = lateral != null ? lateral : 0.9f;  // 0.9F if value not found (matching the vanilla spider)
		}
		hOffset *= scale;
		px -= hOffset * side * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90));
		pz += hOffset * side * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90));

		// 2.Vertical Offset
		int colX = Mth.floor(px), colZ = Mth.floor(pz), feetY = Mth.floor(py);
		boolean canGen = false;
		BlockState blockState = null;
		for (int i = 0; i < 2; i++) {  // 2 loop, first is feetBlock, second is belowBlock.
			BlockPos pos = new BlockPos(colX, feetY - i, colZ);
			blockState = this.level().getBlockState(pos);
			VoxelShape blockShape = blockState.getCollisionShape(this.level(), pos);
			boolean isInBlackList = fpp$isInSet(blockState, CONFIG.getExcludedBlockSet());
			double tempY = Double.NEGATIVE_INFINITY;
			if (fpp$isInSet(blockState, CONFIG.getApplyBlockSet())) {  // Highest priority.
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
		double dx, dz;      // get facing
		//~ if 1.16.5 'this.getDeltaMovement().horizontalDistance() == 0' -> 'this.getDeltaMovement().x == 0 && this.getDeltaMovement().z == 0'
		if (this.getDeltaMovement().horizontalDistance() == 0) {
			dx = -Mth.sin((float) Math.toRadians(this.getRotationVector().y));
			dz =  Mth.cos((float) Math.toRadians(this.getRotationVector().y));
		} else {
			dx = this.getDeltaMovement().x();
			dz = this.getDeltaMovement().z();
		}
		if (canGen) {
			// Fix height by blocks if in...(extra vertical offset)
			Float height = CONFIG.getBlockHeightMap().get(VersionUtil.getBlockName(blockState));
			if (height != null) {
				py += height;
			} else {
				py += VersionUtil.getBlockTags(blockState).stream().map(
						//~ if 1.16.5 'tag.location().' -> 'tag.'
						tag -> CONFIG.getBlockHeightMap().get("#" + tag.location().toString())
				).filter(Objects::nonNull).findFirst().orElse(0f);
			}

			// footprint
			this.level().addParticle(Client.FOOTPRINT.setData((LivingEntity) (Object) this), px, py, pz, dx, 0, dz);
			// Snow Dust
			if (blockState.is(Blocks.SNOW) && fpp$isEnable(CONFIG.getSnowDustLevel())) {
				int i = this.isSprinting() ? 4 : 2;
				int v = this.isSprinting() ? 3 : 10;
				while (--i >= 0) {
					this.level().addParticle(Client.SNOWDUST.setData(scale), px, py, pz,
							(Math.random() - 0.5f) / v,
							0,
							(Math.random() - 0.5f) / v
					);
				}
			}
		} else if (fpp$wetTimer <= CONFIG.getWetDuration() * 20) {
			// waterprint (gen when footprint not gen)
			this.level().addParticle(Client.WATERMARK.setData((LivingEntity) (Object) this), px, py, pz, dx, fpp$wetTimer, dz);		// push timer to calc alpha
		}
		// water splash (gen whatever print gen)
		if (fpp$wetTimer <= CONFIG.getWetDuration() * 20 && fpp$isEnable(CONFIG.getWaterSplashLevel())) {
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
						//~ if 1.16.5 '.horizontalDistance' -> '.length'
						0.02f + Math.random() * this.getDeltaMovement().horizontalDistance(),
						(Math.random() - 0.5f) / v
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
		return hardnessGate > 0 && hardness >= 0 && Mth.abs(hardness) < hardnessGate;
	}

	@Unique
	private boolean fpp$isInSet(BlockState blockState, Set<String> set) {
		return set.contains(VersionUtil.getBlockName(blockState)) ||
		       VersionUtil.getBlockTags(blockState).stream().anyMatch(tag ->
				       //~ if 1.16.5 '.location().toString()' -> '.toString()'
				       set.contains("#" + tag.location().toString()));  // DO NOT remove .toString() you fool! IDE was a liar!
	}

	@Unique
	private boolean fpp$isEnable(WorkMode workMode) {
		//~ if 1.16.5 'this.isAlwaysTicking()' -> '(((LivingEntity) (Object) this) instanceof Player)'
		return workMode == WorkMode.ALL || (workMode == WorkMode.PLAYER_ONLY && this.isAlwaysTicking());
	}
}
//~ }
