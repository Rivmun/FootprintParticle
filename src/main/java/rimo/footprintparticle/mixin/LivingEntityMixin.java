package rimo.footprintparticle.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rimo.footprintparticle.FPPClient;
import rimo.footprintparticle.FootprintParticleType;

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

	@Inject(method = "jump", at = @At("TAIL"), cancellable = true)
	protected void jump(CallbackInfo ci) {
		this.footprintGenerator();
	}

	@Inject(method = "tick", at = @At("TAIL"), cancellable = true)
	public void tick(CallbackInfo ci) {
		if (this.world.isClient && timer-- <= 0) {
			if (!this.isSneaking()) {
				// Either on ground moving or landing
				if ((this.getVelocity().getX() != 0 && this.getVelocity().getZ() != 0 && this.isOnGround()) || (!wasOnGround && this.isOnGround())) {
					this.footprintGenerator();
				}
				wasOnGround = this.isOnGround();
			}
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
			px = px + 0.75f * i * Math.sin(this.getHorizontalFacing().asRotation() / 180 * Math.PI);
			pz = pz + 0.75f * i * Math.cos(this.getHorizontalFacing().asRotation() / 180 * Math.PI);
			timer = (int) (this.getPrimaryPassenger() != null ? this.getPrimaryPassenger().isPlayer() ? timer * 0.5f : timer * 1.33f : timer * 1.33f);
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
			// Fix height by blocks if in...
			try {
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
	
		if (canGen) {
			FootprintParticleType footprint = FPPClient.FOOTPRINT;
			this.world.addParticle(footprint.setData((LivingEntity) (Object) this), px, py, pz, this.getVelocity().getX(), 0, this.getVelocity().getZ());
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
