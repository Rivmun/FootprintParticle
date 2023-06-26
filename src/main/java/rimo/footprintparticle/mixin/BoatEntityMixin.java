package rimo.footprintparticle.mixin;

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
	abstract boolean checkBoatInWater();
	
	@Inject(method = "tick", at = @At("TAIL"), cancellable = true)
	public void tick(CallbackInfo ci) {
		if (this.getVelocity().horizontalLength() > 0.1f) {
			var i = Math.random() > 0.5f ? 1 : -1;		//TODO: boat's yaw is 90 degress more than other entity, strange.
			if (this.checkBoatInWater()) {
				this.getWorld().addParticle(		// at paddle
						ParticleTypes.FALLING_WATER,
						this.getX() + i * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y - 10 + Math.random() * 20)),
						(int) this.getY() + 1f,
						this.getZ() + i * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y - 10 + Math.random() * 20)),
						Math.random() / 5f,
						Math.random() * 5f,
						Math.random() / 5f
				);
				this.getWorld().addParticle(		// at head
						ParticleTypes.RAIN,
						this.getX() + 1.2f * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
						(int) this.getY() + 1f,
						this.getZ() + 1.2f * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
						0,
						0,
						0
				);
				this.getWorld().addParticle(		// at tail
						ParticleTypes.BUBBLE,
						this.getX() - 1.2f * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
						(int) this.getY() + 0.5f + Math.random() / 2f,
						this.getZ() - 1.2f * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
						this.getVelocity().getX() / 5f,
						Math.random() / 3f,
						this.getVelocity().getZ() / 5f
				);
			} else {
				this.getWorld().addParticle(		// at tail
						ParticleTypes.WHITE_ASH,
						this.getX() - 1.2f * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
						this.getY(),
						this.getZ() - 1.2f * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90 + Math.random() * 30 * i)),
						this.getVelocity().getX() / 5f,
						0,
						this.getVelocity().getZ() / 5f
				);
			}
		}
	}
}
