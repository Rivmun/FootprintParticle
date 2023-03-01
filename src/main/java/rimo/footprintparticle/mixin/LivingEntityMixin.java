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

	private int timer;

	@Inject(method = "tick", at = @At("TAIL"), cancellable = true)
	public void tick(CallbackInfo ci) {
		if (this.world.isClient && timer-- <= 0 && FPPClient.CONFIG.isEnable()) {
			if (!FPPClient.CONFIG.getExcludedMobs().contains(EntityType.getId(this.getType()).toString()) && this.isOnGround() && !this.isSneaking()) {
				if (this.getVelocity().getX() != 0 && this.getVelocity().getZ() != 0) {

					var block = this.world.getBlockState(this.getBlockPos());
					var canGen = isPrintCanGen(block);
					if (!canGen) {
						block = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1, this.getZ()));
						canGen = isPrintCanGen(block);
					}

					if (canGen) {
						FootprintParticleType footprint = FPPClient.FOOTPRINT;
						this.world.addParticle(
								footprint.setData(block.getRegistryEntry().getKey().get().getValue().toString()),
								this.getX(),
								block.isOf(Blocks.SNOW) || block.isOf(Blocks.SOUL_SAND)
										? this.getY() + 0.135f
										: this.getY() + 0.01f,
								this.getZ(),
								this.getVelocity().getX(),
								0,
								this.getVelocity().getZ());
						timer = (int) (FPPClient.CONFIG.getSecPerPrint() * 20);
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
				canGen = Math.abs(block.getBlock().getHardness()) < 0.9f && block.isOpaque() && !block.isAir();
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
