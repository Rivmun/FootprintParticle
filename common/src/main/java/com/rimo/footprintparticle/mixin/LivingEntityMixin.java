package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.Util;
import com.rimo.footprintparticle.particle.FootprintParticleType;
import com.rimo.footprintparticle.particle.SnowDustParticleType;
import com.rimo.footprintparticle.particle.WatermarkParticleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
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
	private int fpp$timer = 0;
	@Unique
	private boolean fpp$wasOnGround = true;
	@Unique
	private int fpp$wetTimer = FPPClient.CONFIG.getWetDuration() * 20;

	@Inject(method = "jump", at = @At("TAIL"))
	protected void jump(CallbackInfo ci) {
		this.fpp$footprintGenerator();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (fpp$timer <= 0) {
			if (!this.isSneaking() && !this.isSubmergedInWater()) {
				// Either on ground moving or landing
				if (((this.getVelocity().getX() != 0 || this.getVelocity().getZ() != 0) && this.isOnGround()) || (! fpp$wasOnGround && this.isOnGround())) {
					this.fpp$footprintGenerator();
				}
				fpp$wasOnGround = this.isOnGround();
			}
		} else {
			fpp$timer--;
		}

		if (this.isTouchingWaterOrRain()) {
			fpp$wetTimer = 0;
		} else if (fpp$wetTimer <= FPPClient.CONFIG.getWetDuration() * 20){
			fpp$wetTimer++;
		}

		// Swim Pop
		if (this.isSwimming() &&
				(FPPClient.CONFIG.getSwimPopLevel() == 2 ||
				(FPPClient.CONFIG.getSwimPopLevel() == 1 && ((LivingEntity)(Object)this) instanceof PlayerEntity))) {
			float range = Util.getEntityScale((LivingEntity) (Object) this);
			this.world.addParticle(
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
	public void fpp$footprintGenerator() {
		if (FPPClient.CONFIG.isEnable() == 0 ||
				(FPPClient.CONFIG.isEnable() == 1 && !(((LivingEntity)(Object)this) instanceof PlayerEntity)))
			return;
		if (FPPClient.CONFIG.getExcludedMobs().contains(EntityType.getId(this.getType()).toString()))
			return;
		if (!FPPClient.CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return;
		if (MinecraftClient.getInstance().getNetworkHandler() == null)      // fuck.
			return;

		// Set Interval
		fpp$timer = this.isSprinting() ? (int) (FPPClient.CONFIG.getSecPerPrint() * 13.33f) : (int) (FPPClient.CONFIG.getSecPerPrint() * 20);
		for (String stream : FPPClient.CONFIG.getMobInterval()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getId(this.getType()).toString())) {
				try {
					fpp$timer *= Float.parseFloat(str[1]);
				} catch (Exception e) {
					//
				}
				break;
			}
		}

		// Fix pos...
		double px = this.getX();
		double py = this.getY() + 0.01f + FPPClient.CONFIG.getPrintHeight();
		double pz = this.getZ();
		float scale = Util.getEntityScale((LivingEntity) (Object) this);

		// Horizontal Offset
		// Front and back
		int side = Math.random() > 0.5f ? 1 : -1;
		float hOffset = 0.0625f;
		for (String stream : FPPClient.CONFIG.getHorseLikeMobs()) {
			String[] str = stream.split(",");
			if (str[0].equals(EntityType.getId(this.getType()).toString())) {
				hOffset = 0.75f;
				try {
					hOffset = Float.parseFloat(str[1]);
				} catch (Exception e) {
					//
				}
				fpp$timer = (int) (this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() instanceof PlayerEntity ? fpp$timer * 0.5f : fpp$timer * 1.33f : fpp$timer * 1.33f);
				break;
			}
		}
		hOffset *= scale;
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
		hOffset *= scale;
		px = px - hOffset * side * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90));
		pz = pz + hOffset * side * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90));

		// Check block type...
		BlockPos pos = new BlockPos(px, py, pz);
		boolean canGen = fpp$isPrintCanGen(pos) && this.world.getBlockState(pos).isOpaque();
		if (!canGen) {
			pos = new BlockPos(px, py - 1, pz);
			canGen = fpp$isPrintCanGen(pos) && this.world.getBlockState(pos).isOpaque() && Block.isShapeFullCube(this.world.getBlockState(pos).getCollisionShape(this.world, pos));
		} else {
			// Fix height by blocks if in...
			try {
				BlockState block = this.world.getBlockState(pos);
				for (String str : FPPClient.CONFIG.getBlockHeight()) {
					String[] str2 = str.split(",");
					if (str2[0].charAt(0) == '#') {
						for (Identifier identifier : MinecraftClient.getInstance().getNetworkHandler().getTagManager().getBlocks().getTagsFor(block.getBlock())) {
							if (str2[0].equals(identifier.getPath())) {
								py += Float.parseFloat(str2[1]);
								break;
							}
						}
					} else if (str2[0].contentEquals(Registry.BLOCK.getId(block.getBlock()).toString())) {
						py += Float.parseFloat(str2[1]);
						break;
					}
				}

				// Snow Dust
				if (block.isOf(Blocks.SNOW) &&
						(FPPClient.CONFIG.getSnowDustLevel() == 2 ||
						(FPPClient.CONFIG.getSnowDustLevel() == 1 && (LivingEntity)(Object)this instanceof PlayerEntity))) {
					int i = this.isSprinting() ? 4 : 2;
					int v = this.isSprinting() ? 3 : 10;
					while (--i >= 0) {
						SnowDustParticleType snowdust = FPPClient.SNOWDUST.get();
						this.world.addParticle(snowdust.setData(scale), px, py, pz,
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
		if (this.getVelocity().getX() == 0 && this.getVelocity().getZ() == 0) {
			dx = -MathHelper.sin((float) Math.toRadians(this.getRotationClient().y));
			dz =  MathHelper.cos((float) Math.toRadians(this.getRotationClient().y));
		} else {
			dx = this.getVelocity().getX();
			dz = this.getVelocity().getZ();
		}
		if (canGen) {       // footprint
			FootprintParticleType footprint = FPPClient.FOOTPRINT.get();
			this.world.addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, dx, 0, dz);
		} else if (fpp$wetTimer <= FPPClient.CONFIG.getWetDuration() * 20) {        // waterprint (gen when footprint not gen)
			WatermarkParticleType watermark = FPPClient.WATERMARK.get();
			int i = Math.random() > 0.5f ? 1 : -1;
			this.world.addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, dx * i, fpp$wetTimer, dz * i);		// push timer to calc alpha
		}
		// water splash (gen whatever print gen)
		if (fpp$wetTimer <= FPPClient.CONFIG.getWetDuration() * 20 &&
				(FPPClient.CONFIG.getWaterSplashLevel() == 2 ||
				(FPPClient.CONFIG.getWaterSplashLevel() == 1 && (LivingEntity) (Object) this instanceof PlayerEntity))) {
			float range = Util.getEntityScale((LivingEntity) (Object) this);
			int i = (int)((this.isSprinting() ? 18 : 10) * Math.max((0.7f - (float) fpp$wetTimer / (FPPClient.CONFIG.getWetDuration() * 20)), 0));
			int v = this.isSprinting() ? 3 : 6;
			while (--i > 0) {
				this.world.addParticle(
						FPPClient.WATERSPLASH.get(),
						px - 0.25f * range + Math.random() / 4,
						py,
						pz - 0.25f * range + Math.random() / 4,
						(Math.random() - 0.5f) / v,
						0.02f + Math.random() * this.getVelocity().length(),
						(Math.random() - 0.5f) / v
				);
			}
		}
	}

	@Unique
	private boolean fpp$isPrintCanGen(BlockPos pos) {
		BlockState block = this.world.getBlockState(pos);
		boolean canGen = FPPClient.CONFIG.getApplyBlocks().contains(Registry.BLOCK.getId(block.getBlock()).toString());
		if (!canGen) {
			for (Identifier identifier : MinecraftClient.getInstance().getNetworkHandler().getTagManager().getBlocks().getTagsFor(block.getBlock())) {
				canGen = FPPClient.CONFIG.getApplyBlocks().contains("#" + identifier.getPath());
				if (canGen)
					break;
			}
			if (!canGen) {
				// Hardness Filter. See on https://minecraft.fandom.com/wiki/Breaking#Blocks_by_hardness
				canGen = MathHelper.abs(block.getBlock().getBlastResistance()) < FPPClient.CONFIG.getHardnessGate();
				if (canGen) {
					canGen = !FPPClient.CONFIG.getExcludedBlocks().contains(Registry.BLOCK.getId(block.getBlock()).toString());
					if (canGen) {
						for (Identifier identifier : MinecraftClient.getInstance().getNetworkHandler().getTagManager().getBlocks().getTagsFor(block.getBlock())) {
							canGen = !FPPClient.CONFIG.getExcludedBlocks().contains("#" + identifier.getPath());
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
