package rimo.footprintparticle.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rimo.footprintparticle.FPPClient;
import rimo.footprintparticle.FootprintParticleType;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	private int timer = 0;

	@Inject(method = "jump", at = @At("TAIL"), cancellable = true)
	protected void jump(CallbackInfo ci) {
		this.footprintGenerator();
	}

	@Inject(method = "tick", at = @At("TAIL"), cancellable = true)
	public void tick(CallbackInfo ci) {
		if (this.world.isClient && timer-- <= 0) {
			if (this.getVelocity().getX() != 0 && this.getVelocity().getZ() != 0 && this.isOnGround()) {
				this.footprintGenerator();
			}
		}
	}

	public void footprintGenerator() {
		if (FPPClient.CONFIG.isEnable()) {
			if (!this.isSneaking()) {
				if (!FPPClient.CONFIG.getExcludedMobs().contains(EntityType.getId(this.getType()).toString())) {
					timer = this.isSprinting() ? (int) (FPPClient.CONFIG.getSecPerPrint() * 13.33f) : (int) (FPPClient.CONFIG.getSecPerPrint() * 20);
					var px = this.getX();
					var py = this.getY() + 0.01f;
					var pz = this.getZ();

					if (FPPClient.CONFIG.getHorseLikeMobs().contains(EntityType.getId(this.getType()).toString())) {
						var i = Math.random() > 0.5f ? 1 : -1;
						px = px + 0.75f * i * Math.sin(this.getHorizontalFacing().asRotation() / 180 * Math.PI);
						pz = pz + 0.75f * i * Math.cos(this.getHorizontalFacing().asRotation() / 180 * Math.PI);
						if (this.hasPassengers())
							timer *= 0.5f;
					}
					if (FPPClient.CONFIG.getSpiderLikeMobs().contains(EntityType.getId(this.getType()).toString())) {
						var i = Math.random() > 0.5f ? 1 : -1;
						px = px + 0.9f * i * Math.cos(this.getHorizontalFacing().asRotation() / 180 * Math.PI);
						pz = pz + 0.9f * i * Math.sin(this.getHorizontalFacing().asRotation() / 180 * Math.PI);
						timer *= 0.66f;
					}

					// Check block type...
					var block = this.world.getBlockState(new BlockPos(px, py, pz));
					var canGen = isPrintCanGen(block);
					if (!canGen) {
						block = this.world.getBlockState(new BlockPos(px, py - 1, pz));
						canGen = isPrintCanGen(block);
					} else {
						if (block.isOf(Blocks.SNOW) || block.isOf(Blocks.SOUL_SAND))
							py += 0.125f;
					}

					if (canGen) {
						FootprintParticleType footprint = FPPClient.FOOTPRINT;
						this.world.addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, this.getVelocity().getX(), 0, this.getVelocity().getZ());
					}
				}
			}
		}
	}

	private boolean isPrintCanGen(BlockState block) {
		var canGen = FPPClient.CONFIG.getApplyBlocks().contains(block.getRegistryEntry().getKey().get().getValue().toString());
		if (!canGen) {
			for (TagKey<Block> tag : block.streamTags().toList()) {
				canGen = FPPClient.CONFIG.getApplyBlocks().contains("#" + tag.id().toString());
				if (canGen)
					break;
			}
			if (!canGen) {
				// Hardness Filter. See on https://minecraft.fandom.com/wiki/Breaking#Blocks_by_hardness
				canGen = Math.abs(block.getBlock().getHardness()) < 0.7f && block.isOpaque() && !block.isAir();
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
