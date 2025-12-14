package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatEntityMixin extends Entity {

	public AbstractBoatEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Shadow
	protected abstract boolean checkInWater();

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (FPPClient.CONFIG.isEnableBoatTrail()) {
			int k = (int)(this.getDeltaMovement().horizontalDistance() * 10);
			while (Math.random() < k-- / 5f) {
				var i = Math.random() > 0.5f ? 1 : -1;		//TODO: boat's yaw is 90 degrees more than other entity, strange.
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
								FPPClient.WATERSPLASH,
								this.getX() + i * Mth.cos((float) Math.toRadians(this.getRotationVector().y - 10 + Math.random() * 20)),
								(int) this.getY() + 1f,
								this.getZ() + i * Mth.sin((float) Math.toRadians(this.getRotationVector().y - 10 + Math.random() * 20)),
								(Math.random() - 0.5f) / 4f,
								Math.random() * this.getDeltaMovement().horizontalDistance() / 2f,
								(Math.random() - 0.5f) / 4f
						);
					}
					this.level().addParticle(        // at tail
							ParticleTypes.BUBBLE,
							this.getX() - 1.2f * Mth.cos((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
							(int) this.getY() + 0.5f + Math.random() / 2f,
							this.getZ() - 1.2f * Mth.sin((float) Math.toRadians(this.getRotationVector().y + 90 + Math.random() * 30 * i)),
							this.getDeltaMovement().x() / 5f,
							Math.random() / 3f,
							this.getDeltaMovement().z() / 5f
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
								this.getDeltaMovement().x() / 5f,
								Math.random() / 5f,
								this.getDeltaMovement().z() / 5f
						);
					}
				}
			}
		}
	}
}
