package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

//~ if < 1.21.11 'AbstractBoat.class' -> 'Boat.class'
@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin extends Entity {
	public AbstractBoatMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Shadow protected abstract boolean checkInWater();
	@Unique	private double fpp$ox;
	@Unique	private double fpp$oz;
	@Unique	private boolean fpp$hasLastPos;

	@Inject(method = "tick", at = @At("RETURN"))
	public void fpp$tick(CallbackInfo ci) {
		//~ if < 1.20.1 'this.level()' -> 'this.level'
		Level level = this.level();
		if (!(level instanceof ClientLevel))
			return;
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		double dx = fpp$hasLastPos ? x - fpp$ox : 0;
		double dz = fpp$hasLastPos ? z - fpp$oz : 0;
		fpp$ox = x;
		fpp$oz = z;
		fpp$hasLastPos = true;

		double d = Math.sqrt(dx * dx + dz * dz);
		int count = (int) (d * 10);
		if (!Client.CONFIG.isEnableBoatTrail() || count <= 0)
			return;

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && this.distanceToSqr(minecraft.player) > Client.CONFIG.getGenDistSqr())
			return;

		boolean inWater = this.checkInWater();
		//~ if 1.16.5 'this.getYRot()' -> 'this.yRot'
		float yaw = this.getYRot();
		while (Math.random() < count-- / 5f) {
			int side = Math.random() > 0.5f ? 1 : -1;		// lean the burst to one side.
			if (inWater) {
				// The hull sinks below the surface, so (int) y + 1 keeps the splashes on the water surface.
				// Spawned under it, they would be hidden behind the transparent water.
				float head = (float) Math.toRadians(yaw + Math.random() * 30 * side);
				level.addParticle(		// at head
						ParticleTypes.RAIN,
						x - 1.2f * Mth.sin(head),
						(int) y + 1f,
						z + 1.2f * Mth.cos(head),
						0,
						0,
						0
				);
				for (int j = 0; j < 2; j++) {
					float paddle = (float) Math.toRadians(yaw - 10 + Math.random() * 20);
					level.addParticle(		// at paddle
							Client.WATERSPLASH,
							x + side * Mth.cos(paddle),
							(int) y + 1f,
							z + side * Mth.sin(paddle),
							(Math.random() - 0.5f) / 4f,
							Math.random() * d / 2f,
							(Math.random() - 0.5f) / 4f
					);
				}
				float tail = (float) Math.toRadians(yaw + Math.random() * 30 * side);
				level.addParticle(		// at tail
						ParticleTypes.BUBBLE,
						x + 1.2f * Mth.sin(tail),
						(int) y + 0.5f + Math.random() / 2f,
						z - 1.2f * Mth.cos(tail),
						dx / 5f,
						Math.random() / 3f,
						dz / 5f
				);
			} else if (Math.random() > 0.5f) {
				float paddle = (float) Math.toRadians(yaw - 10 + Math.random() * 20);
				level.addParticle(		// at paddle
						ParticleTypes.CLOUD,
						x + side * Mth.cos(paddle),
						y,
						z + side * Mth.sin(paddle),
						Math.random() / 5f,
						Math.random() / 5f,
						Math.random() / 5f
				);
			} else {
				float tail = (float) Math.toRadians(yaw + Math.random() * 30 * side);
				level.addParticle(		// at tail
						ParticleTypes.CLOUD,
						x + 1.2f * Mth.sin(tail),
						y,
						z - 1.2f * Mth.cos(tail),
						dx / 5f,
						Math.random() / 5f,
						dz / 5f
				);
			}
		}
	}
}
