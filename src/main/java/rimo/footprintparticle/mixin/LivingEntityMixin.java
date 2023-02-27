package rimo.footprintparticle.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
					var block = !this.world.getBlockState(this.getBlockPos()).isOpaque()
							? this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1, this.getZ()))
							: this.world.getBlockState(this.getBlockPos());
					// Hardness info see at https://minecraft.fandom.com/zh/wiki/%E6%8C%96%E6%8E%98#%E6%96%B9%E5%9D%97%E7%A1%AC%E5%BA%A6
					if ((Math.abs(block.getBlock().getHardness()) < 0.9f && block.isOpaque() && !block.isAir())
							|| FPPClient.CONFIG.getApplyBlocks().contains(block.getRegistryEntry().getKey().get().getValue().toString())) {
						FootprintParticleType footprint = FPPClient.FOOTPRINT;
						this.world.addParticle(footprint.setData(EntityType.getId(this.getType()).toString()), this.getX(), this.getY() + 0.00625f, this.getZ(), this.getVelocity().getX(), 0, this.getVelocity().getZ());
						timer = (int) (FPPClient.CONFIG.getSecPerPrint() * 20);
					}
				}
			}
		}
	}
}
