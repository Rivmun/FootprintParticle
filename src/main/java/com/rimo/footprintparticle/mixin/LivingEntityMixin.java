package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.Util;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.rimo.footprintparticle.FPPClient.CONFIG;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Unique
	private final ResourceKey<@NotNull Block> AIR = ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("air"));
	@Unique
	private int timer = 0;
	@Unique
	private boolean wasOnGround = true;
	@Unique
	private int wetTimer = CONFIG.getWetDuration() * 20;

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	protected void jump(CallbackInfo ci) {
		this.footprintGenerator();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (timer <= 0) {
			if (!this.isShiftKeyDown() && !this.isUnderWater()) {
				// Either on ground moving or landing
				if ((this.getDeltaMovement().horizontalDistance() != 0 && this.onGround()) || (!wasOnGround && this.onGround())) {
					this.footprintGenerator();
				}
				wasOnGround = this.onGround();
			}
		} else {
			timer--;
		}

		if (this.isInWaterOrRain()) {
			wetTimer = 0;
		} else if (wetTimer <= CONFIG.getWetDuration() * 20){
			wetTimer++;
		}

		// Swim Pop
		if (this.isSwimming() &&
				(CONFIG.getSwimPopLevel() == 2 ||
						(CONFIG.getSwimPopLevel() == 1 && this.isAlwaysTicking()))) {
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
	public void footprintGenerator() {
		if (CONFIG.isEnable() == 0 ||
				(CONFIG.isEnable() == 1 && !this.isAlwaysTicking()))
			return;
		if (CONFIG.getExcludedMobs().contains(EntityType.getKey(this.getType()).toString()))
			return;
		if (!CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return;

		// Set Interval
		timer = this.isSprinting() ? (int) (CONFIG.getSecPerPrint() * 13.33f) : (int) (CONFIG.getSecPerPrint() * 20);
		for (String stream : CONFIG.getMobInterval()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getKey(this.getType()).toString())) {
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
		var py = this.getY() + 0.01f + CONFIG.getPrintHeight();
		var pz = this.getZ();
		var scale = Util.getEntityScale((LivingEntity) (Object) this);

		// Horizontal Offset
		// Front and back
		var side = Math.random() > 0.5f ? 1 : -1;
		var hOffset = 0.0625f;
		for (String stream : CONFIG.getHorseLikeMobs()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getKey(this.getType()).toString())) {
				hOffset = 0.75f;
				try {
					hOffset = Float.parseFloat(str[1]);
				} catch (Exception e) {
					//
				}
				timer = (int) (this.getControllingPassenger() != null ? this.getControllingPassenger().isAlwaysTicking() ? timer * 0.5f : timer * 1.33f : timer * 1.33f);
				break;
			}
		}
		hOffset *= scale;
		px = px - hOffset * side * Mth.sin((float) Math.toRadians(this.getRotationVector().y));
		pz = pz + hOffset * side * Mth.cos((float) Math.toRadians(this.getRotationVector().y));
		// Left and right
		side = Math.random() > 0.5f ? 1 : -1;
		hOffset = 0.125f;
		for (String stream : CONFIG.getSpiderLikeMobs()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getKey(this.getType()).toString())) {
				hOffset = 0.9f;
				try {
					hOffset = Float.parseFloat(str[1]);
				} catch (Exception e) {
					//
				}
				break;
			}
		}
		hOffset *= scale;
		px = px - hOffset * side * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90));
		pz = pz + hOffset * side * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90));

		// Check block type...
		var pos = new BlockPos(Mth.floor(px), Mth.floor(py), Mth.floor(pz));
		var canGen = isPrintCanGen(pos) && this.level().getBlockState(pos).canOcclude();
		if (!canGen) {
			pos = new BlockPos(Mth.floor(px), Mth.floor(py) - 1, Mth.floor(pz));
			canGen = isPrintCanGen(pos) && this.level().getBlockState(pos).canOcclude() && Block.isShapeFullBlock(this.level().getBlockState(pos).getCollisionShape(this.level(), pos));
		} else {
			// Fix height by blocks if in...
			try {
				var block = this.level().getBlockState(pos);
				for (String str : CONFIG.getBlockHeight()) {
					String[] str2 = str.split(",");
					if (str2[0].charAt(0) == '#') {
						for (TagKey<Block> tag : block.getTags().toList()) {
							if (str2[0].equals("#" + tag.location().toString())) {
								py += Float.parseFloat(str2[1]);
								break;
							}
						}
					} else if (str2[0].contentEquals(block.getBlockHolder().unwrapKey().orElse(AIR).identifier().toString())) {
						py += Float.parseFloat(str2[1]);
						break;
					}
				}

				// Snow Dust
				if (block.is(Blocks.SNOW) &&
						(CONFIG.getSnowDustLevel() == 2 ||
								(CONFIG.getSnowDustLevel() == 1 && this.isAlwaysTicking()))) {
					int i = this.isSprinting() ? 4 : 2;
					int v = this.isSprinting() ? 3 : 10;
					while (--i >= 0) {
						SnowDustParticleType snowdust = FPPClient.SNOWDUST;
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
			FootprintParticleType footprint = FPPClient.FOOTPRINT;
			this.level().addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, dx, 0, dz);
		} else if (wetTimer <= CONFIG.getWetDuration() * 20) {        // waterprint (gen when footprint not gen)
			WatermarkParticleType watermark = FPPClient.WATERMARK;
			var i = Math.random() > 0.5f ? 1 : -1;
			this.level().addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, dx * i, wetTimer, dz * i);		// push timer to calc alpha
		}
		// water splash (gen whatever print gen)
		if (wetTimer <= CONFIG.getWetDuration() * 20 &&
				(CONFIG.getWaterSplashLevel() == 2 ||
						(CONFIG.getWaterSplashLevel() == 1 && this.isAlwaysTicking()))) {
			float range = Util.getEntityScale((LivingEntity) (Object) this);
			int i = (int)((this.isSprinting() ? 18 : 10) * Math.max((0.7f - (float) wetTimer / (CONFIG.getWetDuration() * 20)), 0));
			int v = this.isSprinting() ? 3 : 6;
			while (--i > 0) {
				this.level().addParticle(
						FPPClient.WATERSPLASH,
						px - 0.25f * range + Math.random() / 4,
						py,
						pz - 0.25f * range + Math.random() / 4,
						(Math.random() - 0.5f) / v,
						0.02f + Math.random() * this.getDeltaMovement().horizontalDistance(),
						(Math.random() - 0.5f) / v
				);
			}
		}
	}

	@Unique
	private boolean isPrintCanGen(BlockPos pos) {
		var block = this.level().getBlockState(pos);
		var canGen = CONFIG.getApplyBlocks().contains(block.getBlockHolder().unwrapKey().orElse(AIR).identifier().toString());
		if (!canGen) {
			for (TagKey<Block> tag : block.getTags().toList()) {
				canGen = CONFIG.getApplyBlocks().contains("#" + tag.location());
				if (canGen)
					break;
			}
			if (!canGen) {
				// Hardness Filter. See on https://minecraft.fandom.com/wiki/Breaking#Blocks_by_hardness
				canGen = CONFIG.getHardnessGate() > 0 && Mth.abs(block.getBlock().defaultDestroyTime()) < CONFIG.getHardnessGate();
				if (canGen) {
					canGen = !CONFIG.getExcludedBlocks().contains(block.getBlockHolder().unwrapKey().orElse(AIR).identifier().toString());
					if (canGen) {
						for (TagKey<Block> tag : block.getTags().toList()) {
							canGen = !CONFIG.getExcludedBlocks().contains("#" + tag.location());
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
