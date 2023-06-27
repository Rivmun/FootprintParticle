package rimo.footprintparticle.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rimo.footprintparticle.FPPClient;
import rimo.footprintparticle.particle.FootprintParticleType;
import rimo.footprintparticle.particle.WatermarkParticleType;

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
			if (!this.isSneaking()) {
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
			px = px + 0.75f * i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y));
			pz = pz + 0.75f * i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y));
			timer = (int) (this.getControllingPassenger() != null ? this.getControllingPassenger().isPlayer() ? timer * 0.5f : timer * 1.33f : timer * 1.33f);
		}
		if (FPPClient.CONFIG.getSpiderLikeMobs().contains(EntityType.getId(this.getType()).toString())) {
			var i = Math.random() > 0.5f ? 1 : -1;
			px = px + 0.9f * i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90));
			pz = pz + 0.9f * i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90));
			timer *= 0.66f;
		}

		// Check block type...
		var pos = new BlockPos((int) px, (int) py, (int) pz);
		var canGen = isPrintCanGen(pos) && !this.getWorld().getBlockState(pos).isAir();
		if (!canGen) {
			pos = new BlockPos((int) px, (int) py - 1, (int) pz);
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
			} catch (Exception e) {
				// Ignore...
			}
		}

		// Generate
		if (canGen) {
			FootprintParticleType footprint = FPPClient.FOOTPRINT;
			this.getWorld().addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, this.getVelocity().getX(), 0, this.getVelocity().getZ());
		} else if (wetTimer <= FPPClient.CONFIG.getWetDuration() * 20) {
			WatermarkParticleType watermark = FPPClient.WATERMARK;
			var i = Math.random() > 0.5f ? 1 : -1;
			this.getWorld().addParticle(watermark.setData((LivingEntity) (Object) this), px, py, pz, 
					this.getVelocity().getX() * i,
					wetTimer,		// push timer to calc alpha
					this.getVelocity().getZ() * i
			);
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
