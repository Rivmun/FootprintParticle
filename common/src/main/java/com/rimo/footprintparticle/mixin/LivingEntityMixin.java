package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
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
				if (((this.getVelocity().getX() != 0 || this.getVelocity().getZ() != 0) && this.isOnGround()) || (!wasOnGround && this.isOnGround())) {
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
				(FPPClient.CONFIG.getSwimPopLevel() == 1 && ((LivingEntity)(Object)this) instanceof PlayerEntity))) {
			float range = FPPClient.getEntityScale((LivingEntity) (Object) this);
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
	public void footprintGenerator() {
		if (!FPPClient.CONFIG.isEnable())
			return;
		if (FPPClient.CONFIG.getExcludedMobs().contains(EntityType.getId(this.getType()).toString()))
			return;
		if (!FPPClient.CONFIG.getCanGenWhenInvisible() && this.isInvisible())
			return;
		if (MinecraftClient.getInstance().getNetworkHandler() == null)      // fuck.
			return;

		timer = this.isSprinting() ? (int) (FPPClient.CONFIG.getSecPerPrint() * 13.33f) : (int) (FPPClient.CONFIG.getSecPerPrint() * 20);

		// Fix pos...
		double px = this.getX();
		double py = this.getY() + 0.01f + FPPClient.CONFIG.getPrintHeight();
		double pz = this.getZ();

		// Horse and spider pos set on besides...
		if (FPPClient.CONFIG.getHorseLikeMobs().contains(EntityType.getId(this.getType()).toString())) {
			int i = Math.random() > 0.5f ? 1 : -1;		// Random sides
			px = px - 0.75f * i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y));
			pz = pz + 0.75f * i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y));
			timer = (int) (this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() instanceof PlayerEntity ? timer * 0.5f : timer * 1.33f : timer * 1.33f);
		}
		if (FPPClient.CONFIG.getSpiderLikeMobs().contains(EntityType.getId(this.getType()).toString())) {
			int i = Math.random() > 0.5f ? 1 : -1;
			px = px - 0.90f * i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90));
			pz = pz + 0.90f * i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90));
			timer *= 0.66f;
		}

		// Check block type...
		BlockPos pos = new BlockPos(px, py, pz);
		boolean canGen = isPrintCanGen(pos) && this.world.getBlockState(pos).isOpaque();
		if (!canGen) {
			pos = new BlockPos(px, py - 1, pz);
			canGen = isPrintCanGen(pos) && this.world.getBlockState(pos).isOpaque() && Block.isShapeFullCube(this.world.getBlockState(pos).getCollisionShape(this.world, pos));
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
						this.world.addParticle(snowdust.setData(FPPClient.getEntityScale((LivingEntity) (Object) this)), px, py, pz,
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
		} else if (wetTimer <= FPPClient.CONFIG.getWetDuration() * 20) {        // waterprint (gen when footprint not gen)
			WatermarkParticleType watermark = FPPClient.WATERMARK.get();
			int i = Math.random() > 0.5f ? 1 : -1;
			this.world.addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, dx * i, wetTimer, dz * i);		// push timer to calc alpha
		}
		// water splash (gen whatever print gen)
		if (wetTimer <= FPPClient.CONFIG.getWetDuration() * 20 &&
				(FPPClient.CONFIG.getWaterSplashLevel() == 2 ||
				(FPPClient.CONFIG.getWaterSplashLevel() == 1 && (LivingEntity) (Object) this instanceof PlayerEntity))) {
			float range = FPPClient.getEntityScale((LivingEntity) (Object) this);
			int i = (int)((this.isSprinting() ? 18 : 10) * Math.max((0.7f - (float) wetTimer / (FPPClient.CONFIG.getWetDuration() * 20)), 0));
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
	private boolean isPrintCanGen(BlockPos pos) {
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
				canGen = MathHelper.abs(block.getBlock().getBlastResistance()) < 0.7f;
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
