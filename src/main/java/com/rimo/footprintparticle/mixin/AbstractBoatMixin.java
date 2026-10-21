package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
//~ if < 1.21.11 '.boat.AbstractBoat' -> '.Boat'
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if < 1.20.1 'this.level()' -> 'this.level' {
//~ if < 1.21.11 'AbstractBoat.class' -> 'Boat.class'
@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin extends Entity {

	public AbstractBoatMixin(EntityType<?> type, Level world) {
		super(type, world);
		fpp$ox = this.getX();
		fpp$oz = this.getZ();
	}

	@Shadow
	protected abstract boolean checkInWater();
	@Unique	private double fpp$ox;
	@Unique	private double fpp$oz;

	@Inject(method = "tick", at = @At("TAIL"))
	public void fpp$tick(CallbackInfo ci) {
		// calc deltaMovement by ourselves because .getDeltaMovement() always 0 if entity ridden by remote player.
		double x = this.getX();
		double z = this.getZ();
		double dx = x - fpp$ox;
		double dz = z - fpp$oz;
		double d = Math.sqrt(dx * dx + dz * dz);
		fpp$ox = x;
		fpp$oz = z;

		if (Client.CONFIG.isEnableBoatTrail()) {
			int k = (int)(d * 10);
			while (Math.random() < k-- / 5f) {
				int i = Math.random() > 0.5f ? 1 : -1;		//TODO: boat's yaw is 90 degrees more than other entity, strange.
				if (this.checkInWater()) {
					this.level().addParticle(		// at head
							ParticleTypes.RAIN,
							this.getX() + 1.2f * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
							(int) this.getY() + 1f,
							this.getZ() + 1.2f * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
							0,
							0,
							0
					);
					for (int j = 0; j < 2; j++) {
						this.level().addParticle(        // at paddle
								Client.WATERSPLASH,
								this.getX() + i * Mth.cos((float) Math.toRadians(this.getRotationVector().y - 10 + Math.random() * 20)),
								(int) this.getY() + 1f,
								this.getZ() + i * Mth.sin((float) Math.toRadians(this.getRotationVector().y - 10 + Math.random() * 20)),
								(Math.random() - 0.5f) / 4f,
								Math.random() * d / 2f,
								(Math.random() - 0.5f) / 4f
						);
					}
					this.level().addParticle(        // at tail
							ParticleTypes.BUBBLE,
							this.getX() - 1.2f * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
							(int) this.getY() + 0.5f + Math.random() / 2f,
							this.getZ() - 1.2f * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
							dx / 5f,
							Math.random() / 3f,
							dz / 5f
					);
				} else {
					if (Math.random() > 0.5f) {
						this.level().addParticle(		// at paddle
								ParticleTypes.CLOUD,
								this.getX() + i * Mth.cos((float) Math.toRadians(this.getRotationVector().y - 10 + Math.random() * 20)),
								this.getY(),
								this.getZ() + i * Mth.sin((float) Math.toRadians(this.getRotationVector().y - 10 + Math.random() * 20)),
								Math.random() / 5f,
								Math.random() / 5f,
								Math.random() / 5f
						);
					} else {
						this.level().addParticle(        // at tail
								ParticleTypes.CLOUD,
								this.getX() - 1.2f * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
								this.getY(),
								this.getZ() - 1.2f * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
								dx / 5f,
								Math.random() / 5f,
								dz / 5f
						);
					}
				}
			}
		}
	}
}
//~ }
