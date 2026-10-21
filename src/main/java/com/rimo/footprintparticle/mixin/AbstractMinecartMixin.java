package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
//~ if < 1.21.11 '.minecart.AbstractMinecart' -> '.AbstractMinecart'
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if < 1.20.1 'this.level()' -> 'this.level' {
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity {
	public AbstractMinecartMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Unique private int fpp$timer = 0;
	@Unique	private double fpp$ox;
	@Unique	private double fpp$oz;
	@Unique	private boolean fpp$hasLastPos;

	@Inject(method = "tick", at = @At("RETURN"))
	public void fpp$tick(CallbackInfo ci) {
		// calc deltaMovement by ourselves because .getDeltaMovement() always 0 if entity ridden by remote player.
		double x = this.getX();
		double z = this.getZ();
		double dx = fpp$hasLastPos ? x - fpp$ox : 0;
		double dz = fpp$hasLastPos ? z - fpp$oz : 0;
		fpp$ox = x;
		fpp$oz = z;
		fpp$hasLastPos = true;	// entities read from disk get their position after construction, so ignore the first delta.

		if (fpp$timer > 0) {
			fpp$timer--;
			return;
		}
		if (dx * dx + dz * dz <= 1.0E-8)  // d <= 1.0E-4
			return;

		Level level = this.level();
		BlockState state = level.getBlockState(this.blockPosition());
		float secPerPrint = Client.CONFIG.getSecPerPrint();
		fpp$timer = state.is(Blocks.POWERED_RAIL)?
				(int) (secPerPrint * 3.33f):
				(int) (secPerPrint * 6.66f);

		if (!state.is(BlockTags.RAILS) || Math.random() > Client.CONFIG.getRailFlameRange())
			return;

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && this.distanceToSqr(minecraft.player) > 64.0 * 64.0)
			return;

		int side = Math.random() > 0.5f ? 1 : -1;
		//~ if 1.16.5 'this.getYRot()' -> 'this.yRot'
		float yaw = (float) Math.toRadians(this.getYRot());
		float facingX = -Mth.sin(yaw);
		float facingZ = Mth.cos(yaw);
		level.addParticle(
				//~ if 1.16.5 'ELECTRIC_SPARK' -> 'FALLING_LAVA'
				ParticleTypes.ELECTRIC_SPARK,
				x + side * 0.4f * facingX,
				this.getY() + 0.0625f,
				z + side * 0.4f * facingZ,
				dx / 3f * Math.random(),
				Math.random() / 3,
				dz / 3f * Math.random()
		);
	}
}
//~ }
