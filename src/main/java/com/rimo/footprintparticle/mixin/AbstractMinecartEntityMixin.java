package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartEntityMixin extends Entity {

	public AbstractMinecartEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Unique
	private int timer = 0;

	//TODO: inject at TAIL will do nothing, why?
	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (timer-- <= 0 && this.getDeltaMovement().horizontalDistance() != 0) {
			if (this.level().getBlockState(this.blockPosition()).is(BlockTags.RAILS) && Math.random() <= FPPClient.CONFIG.getRailFlameRange()) {
				var i = Math.random() > 0.5f ? 1 : -1;
				this.level().addParticle(
						ParticleTypes.ELECTRIC_SPARK,
						this.getX() + i * 0.4f * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90)),
						this.getY() + 0.0625f,
						this.getZ() + i * 0.4f * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90)),
						this.getDeltaMovement().x() / 3f * Math.random(),
						Math.random() / 3,
						this.getDeltaMovement().z() / 3f * Math.random()
				);
			}
			timer = this.level().getBlockState(this.blockPosition()).is(Blocks.POWERED_RAIL)?
					(int) (FPPClient.CONFIG.getSecPerPrint() * 3.33f):
					(int) (FPPClient.CONFIG.getSecPerPrint() * 6.66f);
		}
	}
}
