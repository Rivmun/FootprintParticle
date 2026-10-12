package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
//~ if < 1.21.11 '.minecart.AbstractMinecart' -> '.AbstractMinecart'
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if < 1.20.1 'this.level()' -> 'this.level' {
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartEntityMixin extends Entity {

	public AbstractMinecartEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
		fpp$ox = this.getX();
		fpp$oz = this.getZ();
	}

	@Unique
	private int fpp$timer = 0;
	@Unique	private double fpp$ox;
	@Unique	private double fpp$oz;

	//TODO: inject at TAIL will do nothing, why?
	@Inject(method = "tick", at = @At("HEAD"))
	public void fpp$tick(CallbackInfo ci) {
		// calc deltaMovement by ourselves because .getDeltaMovement() always 0 if entity ridden by remote player.
		double x = this.getX();
		double z = this.getZ();
		double dx = x - fpp$ox;
		double dz = z - fpp$oz;
		double d = Math.sqrt(dx * dx + dz * dz);
		fpp$ox = x;
		fpp$oz = z;

		if (fpp$timer-- <= 0 && d > 1.0E-4) {
			if (this.level().getBlockState(this.blockPosition()).is(BlockTags.RAILS) && Math.random() <= Client.CONFIG.getRailFlameRange()) {
				int i = Math.random() > 0.5f ? 1 : -1;
				this.level().addParticle(
						//~ if 1.16.5 'ELECTRIC_SPARK' -> 'FALLING_LAVA'
						ParticleTypes.ELECTRIC_SPARK,
						this.getX() + i * 0.4f * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90)),
						this.getY() + 0.0625f,
						this.getZ() + i * 0.4f * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90)),
						dx / 3f * Math.random(),
						Math.random() / 3,
						dz / 3f * Math.random()
				);
			}
			fpp$timer = this.level().getBlockState(this.blockPosition()).is(Blocks.POWERED_RAIL)?
					(int) (Client.CONFIG.getSecPerPrint() * 3.33f):
					(int) (Client.CONFIG.getSecPerPrint() * 6.66f);
		}
	}
}
//~ }
