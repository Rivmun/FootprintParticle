package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.Util;
import com.rimo.footprintparticle.VersionUtil;
import com.rimo.footprintparticle.config.WorkMode;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
*///? } else {
import net.minecraft.tags.TagKey;
//? }

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

		// Fix pos...
		double px = this.getX();
		double py = this.getY();
		double pz = this.getZ();
		float scale = Util.getEntityScale((LivingEntity) (Object) this);

		// Horizontal Offset
		// Front and back
		int side = Math.random() > 0.5f ? 1 : -1;
		double hOffset = 0.0625f;  // default f/b offset for all entity to decrease repetition.
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
		// Left and right
		side = Math.random() > 0.5f ? 1 : -1;
		hOffset = 0.125f;  // default l/r offset for all entity to decrease repetition.
		java.util.Map<String, Float> spiderMap = CONFIG.getSpiderLikeMobsMap();
		if (spiderMap.containsKey(id)) {
			Float lateral = spiderMap.get(id);
			hOffset = lateral != null ? lateral : 0.9f;  // 0.9F if value not found (matching the vanilla spider)
		}
		hOffset *= scale;
		px -= hOffset * side * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90));
		pz += hOffset * side * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90));

		// Vertical Offset
		int colX = Mth.floor(px), colZ = Mth.floor(pz), feetY = Mth.floor(py);
		BlockPos pos;
		boolean canGen;
		BlockPos feetPos = new BlockPos(colX, feetY, colZ);
		BlockState feetState = this.level().getBlockState(feetPos);
		VoxelShape feetShape = feetState.getCollisionShape(this.level(), feetPos);
		if (!feetShape.isEmpty()) {  // if feet block has collision shape...
			pos = feetPos;
			double tempY = feetY + feetShape.max(Direction.Axis.Y);
			py = tempY > py ? py + 0.01F : tempY + 0.01F;  // never higher than feet.
			canGen = fpp$isPrintCanGen(pos);
		} else if (!feetState.isAir() && feetState.canOcclude()) {  // if no colShape, no air, but occlusion...(i mean, snowLayer)
			pos = feetPos;
			py = feetY + 0.01f;
			canGen = fpp$isPrintCanGen(pos);
		} else {  // no colShape, air or cant occlude... should be fragile, misc blocks...
			pos = new BlockPos(colX, feetY - 1, colZ);  // move detector to below feet
			BlockState belowState = this.level().getBlockState(pos);
			VoxelShape belowShape = belowState.getCollisionShape(this.level(), pos);
			canGen = !belowShape.isEmpty() && fpp$isPrintCanGen(pos);
			if (canGen) {
				if (landedOrJump) {
					canGen = belowShape.max(Direction.Axis.X) - belowShape.min(Direction.Axis.X) > 0.999
							&& belowShape.max(Direction.Axis.Z) - belowShape.min(Direction.Axis.Z) > 0.999
							&& !(Block.isShapeFullBlock(belowShape) && !belowState.canOcclude());
				} else {
					canGen = py - feetY < 0.05 && belowState.canOcclude() && Block.isShapeFullBlock(belowShape);
				}
			}
			py = feetY - 1 + (belowShape.isEmpty() ? 0.0 : belowShape.max(Direction.Axis.Y)) + 0.01f;
		}

		if (canGen) {
			// Fix height by blocks if in...
			BlockState block = this.level().getBlockState(pos);
			String blockId = VersionUtil.getBlockName(block);
			Float height = CONFIG.getBlockHeightMap().get(blockId);
			if (height != null) {
				py += height;
			} else {
				//~ if 1.16.5 'TagKey<Block>' -> 'ResourceLocation'
				for (TagKey<Block> tag : VersionUtil.getBlockTags(block)) {
					//~ if 1.16.5 '.location' -> '.getPath'
					Float tagHeight = CONFIG.getBlockHeightMap().get("#" + tag.location());
					if (tagHeight != null)
						py += tagHeight;
				}
			}

			// Snow Dust
			if (block.is(Blocks.SNOW) && fpp$isEnable(CONFIG.getSnowDustLevel())) {
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
		}

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
		if (canGen) {       // footprint
			FootprintParticleType footprint = Client.FOOTPRINT;
			this.level().addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, dx, 0, dz);
		} else if (fpp$wetTimer <= CONFIG.getWetDuration() * 20) {        // waterprint (gen when footprint not gen)
			WatermarkParticleType watermark = Client.WATERMARK;
			int i = Math.random() > 0.5f ? 1 : -1;
			this.level().addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, dx * i, fpp$wetTimer, dz * i);		// push timer to calc alpha
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

	@Unique
	private boolean fpp$isPrintCanGen(BlockPos pos) {
		BlockState block = this.level().getBlockState(pos);
		String blockId = VersionUtil.getBlockName(block);
		// White Block List
		boolean canGen = CONFIG.getApplyBlockSet().contains(blockId);
		if (!canGen) {
			//~ if 1.16.5 'TagKey<Block>' -> 'ResourceLocation'
			for (TagKey<Block> tag : VersionUtil.getBlockTags(block)) {
				//~ if 1.16.5 '.location' -> '.getPath'
				canGen = CONFIG.getApplyBlockSet().contains("#" + tag.location());
				if (canGen)
					break;
			}
			if (!canGen) {
				// Hardness Filter. See on https://minecraft.fandom.com/wiki/Breaking#Blocks_by_hardness
				//~ if 1.16.5 '.defaultDestroyTime' -> '.getExplosionResistance'
				float hardness = block.getBlock().defaultDestroyTime();
				float hardnessGate = CONFIG.getHardnessGate();
				canGen = hardnessGate > 0 && hardness >= 0 && Mth.abs(hardness) < hardnessGate;
				if (canGen) {
					// Black Block List
					canGen = !CONFIG.getExcludedBlockSet().contains(blockId);
					if (canGen) {
						//~ if 1.16.5 'TagKey<Block>' -> 'ResourceLocation'
						for (TagKey<Block> tag : VersionUtil.getBlockTags(block)) {
							//~ if 1.16.5 '.location' -> '.getPath'
							canGen = !CONFIG.getExcludedBlockSet().contains("#" + tag.location());
							if (!canGen)
								break;
						}
					}
				}
			}
		}
		return canGen;
	}

	@Unique
	private boolean fpp$isEnable(WorkMode workMode) {
		//~ if 1.16.5 'this.isAlwaysTicking()' -> '(((LivingEntity) (Object) this) instanceof Player)'
		return workMode == WorkMode.ALL || (workMode == WorkMode.PLAYER_ONLY && this.isAlwaysTicking());
	}
}
//~ }
