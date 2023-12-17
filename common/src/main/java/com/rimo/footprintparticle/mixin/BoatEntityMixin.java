package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {

	public BoatEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	protected abstract boolean checkBoatInWater();

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (FPPClient.CONFIG.isEnableBoatTrail()) {
			int k = (int)(this.getVelocity().horizontalLength() * 10);
			while (Math.random() < k-- / 5f) {
				var i = Math.random() > 0.5f ? 1 : -1;		//TODO: boat's yaw is 90 degrees more than other entity, strange.
				if (this.checkBoatInWater()) {
					this.getWorld().addParticle(		// at head
							ParticleTypes.RAIN,
							this.getX() + 1.2f * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
							(int) this.getY() + 1f,
							this.getZ() + 1.2f * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
							0,
							0,
							0
					);
					for (int j = 0; j < 2; j++) {
						this.getWorld().addParticle(        // at paddle
								FPPClient.WATERSPLASH.get(),
								this.getX() + i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y - 10 + Math.random() * 20)),
								(int) this.getY() + 1f,
								this.getZ() + i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y - 10 + Math.random() * 20)),
								(Math.random() - 0.5f) / 4f,
								Math.random() * this.getVelocity().horizontalLength() / 2f,
								(Math.random() - 0.5f) / 4f
						);
					}
					this.getWorld().addParticle(        // at tail
							ParticleTypes.BUBBLE,
							this.getX() - 1.2f * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
							(int) this.getY() + 0.5f + Math.random() / 2f,
							this.getZ() - 1.2f * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
							this.getVelocity().getX() / 5f,
							Math.random() / 3f,
							this.getVelocity().getZ() / 5f
					);
				} else {
					if (Math.random() > 0.5f) {
						this.getWorld().addParticle(		// at paddle
								ParticleTypes.CLOUD,
								this.getX() + i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y - 10 + Math.random() * 20)),
								this.getY(),
								this.getZ() + i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y - 10 + Math.random() * 20)),
								Math.random() / 5f,
								Math.random() / 5f,
								Math.random() / 5f
						);
					} else {
						this.getWorld().addParticle(        // at tail
								ParticleTypes.CLOUD,
								this.getX() - 1.2f * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
								this.getY(),
								this.getZ() - 1.2f * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
								this.getVelocity().getX() / 5f,
								Math.random() / 5f,
								this.getVelocity().getZ() / 5f
						);
					}
				}
			}
		}
	}
}
