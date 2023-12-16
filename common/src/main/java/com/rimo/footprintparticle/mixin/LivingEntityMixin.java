package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.Util;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Unique
	private int timer = 0;
	@Unique
	private boolean wasOnGround = true;
	@Unique
	private int wetTimer = FPPClient.CONFIG.getWetDuration() * 20;

	@Inject(method = "jump", at = @At("TAIL"))
	protected void jump(CallbackInfo ci) {
		this.footprintGenerator();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (timer <= 0) {
			if (!this.isSneaking() && !this.isSubmergedInWater()) {
				// Either on ground moving or landing
				if ((this.getVelocity().horizontalLength() != 0 && this.isOnGround()) || (!wasOnGround && this.isOnGround())) {
					this.footprintGenerator();
				}
				wasOnGround = this.isOnGround();
			}
		} else {
			timer--;
		}

		if (this.isTouchingWaterOrRain()) {
			wetTimer = 0;
		} else if (wetTimer <= FPPClient.CONFIG.getWetDuration() * 20){
			wetTimer++;
		}

		// Swim Pop
		if (this.isSwimming() &&
				(FPPClient.CONFIG.getSwimPopLevel() == 2 ||
				(FPPClient.CONFIG.getSwimPopLevel() == 1 && this.isPlayer()))) {
			float range = Util.getEntityScale((LivingEntity) (Object) this);
			this.getWorld().addParticle(
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
	public void footprintGenerator() {
		if (FPPClient.CONFIG.isEnable() == 0 ||
				(FPPClient.CONFIG.isEnable() == 1 && !this.isPlayer()))
			return;
		if (FPPClient.CONFIG.getExcludedMobs().contains(EntityType.getId(this.getType()).toString()))
			return;
		if (!FPPClient.CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return;

		// Set Interval
		timer = this.isSprinting() ? (int) (FPPClient.CONFIG.getSecPerPrint() * 13.33f) : (int) (FPPClient.CONFIG.getSecPerPrint() * 20);
		for (String stream : FPPClient.CONFIG.getMobInterval()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getId(this.getType()).toString())) {
				try {
					timer *= Float.parseFloat(str[1]);
				} catch (Exception e) {
					//
				}
				break;
			}
		}

		// Fix pos...
		var px = this.getX();
		var py = this.getY() + 0.01f + FPPClient.CONFIG.getPrintHeight();
		var pz = this.getZ();

		// Horizontal Offset
		// Front and back
		var side = Math.random() > 0.5f ? 1 : -1;
		var hOffset = 0.0625f;
		for (String stream : FPPClient.CONFIG.getHorseLikeMobs()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getId(this.getType()).toString())) {
				hOffset = 0.75f;
				try {
					hOffset = Float.parseFloat(str[1]);
				} catch (Exception e) {
					//
				}
				timer = (int) (this.getControllingPassenger() != null ? this.getControllingPassenger().isPlayer() ? timer * 0.5f : timer * 1.33f : timer * 1.33f);
				break;
			}
		}
		px = px - hOffset * side * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y));
		pz = pz + hOffset * side * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y));
		// Left and right
		side = Math.random() > 0.5f ? 1 : -1;
		hOffset = 0.125f;
		for (String stream : FPPClient.CONFIG.getSpiderLikeMobs()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getId(this.getType()).toString())) {
				hOffset = 0.9f;
				try {
					hOffset = Float.parseFloat(str[1]);
				} catch (Exception e) {
					//
				}
				break;
			}
		}
		px = px - hOffset * side * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90));
		pz = pz + hOffset * side * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90));

		// Check block type...
		var pos = new BlockPos(MathHelper.floor(px), MathHelper.floor(py), MathHelper.floor(pz));
		var canGen = isPrintCanGen(pos) && this.getWorld().getBlockState(pos).isOpaque();
		if (!canGen) {
			pos = new BlockPos(MathHelper.floor(px), MathHelper.floor(py) - 1, MathHelper.floor(pz));
			canGen = isPrintCanGen(pos) && this.getWorld().getBlockState(pos).isOpaque() && Block.isShapeFullCube(this.getWorld().getBlockState(pos).getCollisionShape(this.getWorld(), pos));
		} else {
			// Fix height by blocks if in...
			try {
				var block = this.getWorld().getBlockState(pos);
				for (String str : FPPClient.CONFIG.getBlockHeight()) {
					String[] str2 = str.split(",");
					if (str2[0].charAt(0) == '#') {
						for (TagKey<Block> tag : block.streamTags().toList()) {
							if (str2[0].equals("#" + tag.id().toString())) {
								py += Float.parseFloat(str2[1]);
								break;
							}
						}
					} else if (str2[0].contentEquals(block.getRegistryEntry().getKey().get().getValue().toString())) {
						py += Float.parseFloat(str2[1]);
						break;
					}
				}

				// Snow Dust
				if (block.isOf(Blocks.SNOW) &&
						(FPPClient.CONFIG.getSnowDustLevel() == 2 ||
								(FPPClient.CONFIG.getSnowDustLevel() == 1 && this.isPlayer()))) {
					int i = this.isSprinting() ? 4 : 2;
					int v = this.isSprinting() ? 3 : 10;
					while (--i >= 0) {
						SnowDustParticleType snowdust = FPPClient.SNOWDUST.get();
						this.getWorld().addParticle(snowdust.setData(Util.getEntityScale((LivingEntity) (Object) this)), px, py, pz,
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
		if (this.getVelocity().horizontalLength() == 0) {
			dx = -MathHelper.sin((float) Math.toRadians(this.getRotationClient().y));
			dz =  MathHelper.cos((float) Math.toRadians(this.getRotationClient().y));
		} else {
			dx = this.getVelocity().getX();
			dz = this.getVelocity().getZ();
		}
		if (canGen) {       // footprint
			FootprintParticleType footprint = FPPClient.FOOTPRINT.get();
			this.getWorld().addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, dx, 0, dz);
		} else if (wetTimer <= FPPClient.CONFIG.getWetDuration() * 20) {        // waterprint (gen when footprint not gen)
			WatermarkParticleType watermark = FPPClient.WATERMARK.get();
			var i = Math.random() > 0.5f ? 1 : -1;
			this.getWorld().addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, dx * i, wetTimer, dz * i);		// push timer to calc alpha
		}
		// water splash (gen whatever print gen)
		if (wetTimer <= FPPClient.CONFIG.getWetDuration() * 20 &&
				(FPPClient.CONFIG.getWaterSplashLevel() == 2 ||
						(FPPClient.CONFIG.getWaterSplashLevel() == 1 && this.isPlayer()))) {
			float range = Util.getEntityScale((LivingEntity) (Object) this);
			int i = (int)((this.isSprinting() ? 18 : 10) * Math.max((0.7f - (float) wetTimer / (FPPClient.CONFIG.getWetDuration() * 20)), 0));
			int v = this.isSprinting() ? 3 : 6;
			while (--i > 0) {
				this.getWorld().addParticle(
						FPPClient.WATERSPLASH.get(),
						px - 0.25f * range + Math.random() / 4,
						py,
						pz - 0.25f * range + Math.random() / 4,
						(Math.random() - 0.5f) / v,
						0.02f + Math.random() * this.getVelocity().horizontalLength(),
						(Math.random() - 0.5f) / v
				);
			}
		}
	}

	@Unique
	private boolean isPrintCanGen(BlockPos pos) {
		var block = this.getWorld().getBlockState(pos);
		var canGen = FPPClient.CONFIG.getApplyBlocks().contains(block.getRegistryEntry().getKey().get().getValue().toString());
		if (!canGen) {
			for (TagKey<Block> tag : block.streamTags().toList()) {
				canGen = FPPClient.CONFIG.getApplyBlocks().contains("#" + tag.id().toString());
				if (canGen)
					break;
			}
			if (!canGen) {
				// Hardness Filter. See on https://minecraft.fandom.com/wiki/Breaking#Blocks_by_hardness
				canGen = MathHelper.abs(block.getBlock().getHardness()) < 0.7f;
				if (canGen) {
					canGen = !FPPClient.CONFIG.getExcludedBlocks().contains(block.getRegistryEntry().getKey().get().getValue().toString());
					if (canGen) {
						for (TagKey<Block> tag : block.streamTags().toList()) {
							canGen = !FPPClient.CONFIG.getExcludedBlocks().contains("#" + tag.id().toString());
							if (!canGen)
								break;
						}
					}
				}
			}
		}
		return canGen;
	}

}
