package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	private int timer = 0;
	private boolean wasOnGround = true;
	private int wetTimer = FPPClient.CONFIG.getWetDuration() * 20;

	@Inject(method = "jump", at = @At("TAIL"), cancellable = true)
	protected void jump(CallbackInfo ci) {
		this.footprintGenerator();
	}

	@Inject(method = "tick", at = @At("TAIL"), cancellable = true)
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
		if (FPPClient.CONFIG.isEnableSwimPop() && this.isSwimming())
			this.getWorld().addParticle(
					ParticleTypes.BUBBLE,
					this.getX() + Math.random() - 0.5f,
					this.getY() + Math.random() - 0.5f,
					this.getZ() + Math.random() - 0.5f,
					0,
					Math.random() / 10f,
					0
			);
	}

	public void footprintGenerator() {
		if (!FPPClient.CONFIG.isEnable())
			return;
		if (FPPClient.CONFIG.getExcludedMobs().contains(EntityType.getId(this.getType()).toString()))
			return;
		if (!FPPClient.CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return;

		timer = this.isSprinting() ? (int) (FPPClient.CONFIG.getSecPerPrint() * 13.33f) : (int) (FPPClient.CONFIG.getSecPerPrint() * 20);

		// Fix pos...
		var px = this.getX();
		var py = this.getY() + 0.01f + FPPClient.CONFIG.getPrintHeight();
		var pz = this.getZ();

		// Horse and spider pos set on besides...
		if (FPPClient.CONFIG.getHorseLikeMobs().contains(EntityType.getId(this.getType()).toString())) {
			var i = Math.random() > 0.5f ? 1 : -1;		// Random sides
			px = px - 0.75f * i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y));
			pz = pz + 0.75f * i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y));
			timer = (int) (this.getPrimaryPassenger() != null ? this.getPrimaryPassenger().isPlayer() ? timer * 0.5f : timer * 1.33f : timer * 1.33f);
		}
		if (FPPClient.CONFIG.getSpiderLikeMobs().contains(EntityType.getId(this.getType()).toString())) {
			var i = Math.random() > 0.5f ? 1 : -1;
			px = px - 0.90f * i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90));
			pz = pz + 0.90f * i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90));
			timer *= 0.66f;
		}

		// Check block type...
		var pos = new BlockPos(px, py, pz);
		var canGen = isPrintCanGen(pos) && !this.getWorld().getBlockState(pos).isAir();
		if (!canGen) {
			pos = new BlockPos(px, py - 1, pz);
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
				if (FPPClient.CONFIG.isEnableSnowDust() && block.isOf(Blocks.SNOW))
					for (int i = 0; i < 2; i++)
						this.getWorld().addParticle(ParticleTypes.CLOUD, px, py, pz,
								(Math.random() - 0.5f) / 10f,
								0,
								(Math.random() - 0.5f) / 10f
						);

			} catch (Exception e) {
				// Ignore...
			}
		}

		// Generate
		double dx, dz;
		if (this.getVelocity().horizontalLength() == 0) {
			dx = -MathHelper.sin((float) Math.toRadians(this.getRotationClient().y));
			dz =  MathHelper.cos((float) Math.toRadians(this.getRotationClient().y));
		} else {
			dx = this.getVelocity().getX();
			dz = this.getVelocity().getZ();
		}
		if (canGen) {
			FootprintParticleType footprint = FPPClient.FOOTPRINT.get();
			this.getWorld().addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, dx, 0, dz);
		} else if (wetTimer <= FPPClient.CONFIG.getWetDuration() * 20) {
			WatermarkParticleType watermark = FPPClient.WATERMARK.get();
			var i = Math.random() > 0.5f ? 1 : -1;
			this.getWorld().addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, dx * i, wetTimer, dz * i);		// push timer to calc alpha
		}
	}

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