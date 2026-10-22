package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.VersionUtil;
import com.rimo.footprintparticle.config.WorkMode;
import net.minecraft.client.Minecraft;
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
*///? }

import java.util.List;

import static com.rimo.footprintparticle.Client.CONFIG;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Unique private int fpp$timer = 0;
	@Unique private boolean fpp$wasFalling;
	@Unique private boolean fpp$hasLastPos;
	@Unique private int fpp$wetTimer = CONFIG.getWetDuration() * 20;
	@Unique private double fpp$lastX, fpp$lastZ, fpp$lastY;

	@Inject(method = "tick", at = @At("TAIL"))
	public void fpp$tick(CallbackInfo ci) {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		// Entities read from disk get their position after construction, so the first tick only samples the position.
		boolean moved = fpp$hasLastPos
				&& Math.abs(x - this.fpp$lastX) + Math.abs(z - this.fpp$lastZ) > 1.0E-4;
		// Remote player using lerp to calc its pos that cannot get onGround() in accuracy, so we detect whether the dy is flipped.
		double dy = fpp$hasLastPos ? y - this.fpp$lastY : 0;
		// -0.25 is the velocity Y after player freefall around 0.5 blocks. Equivalent to we've filtered out the falling distance lower than the 0.5 blocks.
		boolean falling = dy < -0.25;
		boolean landed = fpp$wasFalling && !falling;
		this.fpp$lastX = x;
		this.fpp$lastY = y;
		this.fpp$lastZ = z;
		fpp$hasLastPos = true;
		fpp$wasFalling = falling;

		// Vanilla only sends world particles to players within 32 blocks; ours are client-side, so cap them at 64 blocks.
		Minecraft minecraft = Minecraft.getInstance();
		boolean near = minecraft.player == null || this.distanceToSqr(minecraft.player) <= CONFIG.getGenDistSqr();

		boolean generated = false;
		if (near && !this.isShiftKeyDown() && !this.isUnderWater()) {
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
		if (near && this.isSwimming() && fpp$isEnable(CONFIG.getSwimPopLevel())) {
			float range = VersionUtil.getEntityScale((LivingEntity) (Object) this);
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
		List<String> tags = VersionUtil.getEntityTagNames(this.getType());
		if (CONFIG.isBlackListMob(id, tags))
			return false;
		if (!CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return false;
		//~ if < 1.20.1 'this.level()' -> 'this.level'
		Level level = this.level();
		double px = this.getX();
		double py = this.getY();
		double pz = this.getZ();
		//~ if 1.16.5 'this.getYRot()' -> 'this.yRot'
		float yawRad = (float) Math.toRadians(this.getYRot());
		float dx = - Mth.sin(yawRad);  // horizontal facing vector.
		float dz =   Mth.cos(yawRad);
		LivingEntity entity = (LivingEntity) (Object) this;
		float scale = VersionUtil.getEntityScale(id, entity);
		boolean isSprinting = this.isSprinting();

		// Set Interval
		fpp$timer = (int) (CONFIG.getSecPerPrint() * (isSprinting ? 13.33F : 20F));
		fpp$timer *= CONFIG.getMobInterval(id, tags);

		// Fix pos. 2 steps:
		// 1.Horizontal Offset
		// 1.1 Front and back
		float front = CONFIG.getMobFront(id, tags);
		if (! Float.isNaN(front)) {
			Entity passenger = this.getControllingPassenger();  // speed up the generation if ridden by player.
			//~ if 1.16.5 'passenger != null && passenger.isAlwaysTicking()' -> 'passenger instanceof Player'
			fpp$timer *= (passenger != null && passenger.isAlwaysTicking() ? 0.5f : 1.33f);
		}
		else front = 0.075f;  // default f/b offset to decrease repetition.
		double fB = front * scale * (Math.random() > 0.5f ? 1 : -1);  // random a side.
		// 1.2 Left and right
		float lateral = CONFIG.getMobLateral(id, tags);
		if (Float.isNaN(lateral)) lateral = 0.15f;  // default l/r offset (for player).
		double lR = lateral * scale * (Math.random() > 0.5f ? 1 : -1);
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
			boolean isInBlackList = CONFIG.isBlackListBlock(blockName, blockTagNames);
			double tempY = Double.NEGATIVE_INFINITY;

			// Wish you can understand what you have written here in the future :P
			if (CONFIG.isWhiteListBlock(blockName, blockTagNames)) {  // Highest priority.
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
			py += CONFIG.getBlockHeight(blockName, blockTagNames);  // Fix height by blocks if in...(extra vertical offset)
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
			BlockPos pos = new BlockPos(colX, Mth.floor(py - 0.02F), colZ);
			if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty())
				return false;  // supported block must have shape. if not, jump the water splash.
			// waterprint (gen when footprint not gen)
			level.addParticle(Client.WATERMARK.setStartAlpha(fpp$wetTimer).setEntity(entity),   // push timer to calc alpha
					px, py, pz, dx, scale, dz);
		}
		// water splash (gen any print was generated with wet)
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
		return hardness >= 0 && hardness < hardnessGate;  // implicitly included 'hardnessGate > 0'.
	}

	@Unique
	private boolean fpp$isEnable(WorkMode workMode) {
		//~ if 1.16.5 'this.isAlwaysTicking()' -> '(((LivingEntity) (Object) this) instanceof Player)'
		return workMode == WorkMode.ALL || (workMode == WorkMode.PLAYER_ONLY && this.isAlwaysTicking());
	}
}
