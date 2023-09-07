package com.rimo.footprintparticle.mixin;

import com.rimo.footprintparticle.FPPClient;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {

	public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Unique
	private int timer = 0;

	//TODO: inject at TAIL will do nothing, why?
	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (timer-- <= 0 && this.getVelocity().horizontalLength() != 0) {
			if (this.getWorld().getBlockState(this.getBlockPos()).isIn(BlockTags.RAILS) && Math.random() <= FPPClient.CONFIG.getRailFlameRange()) {
				var i = Math.random() > 0.5f ? 1 : -1;
				this.getWorld().addParticle(
						ParticleTypes.ELECTRIC_SPARK,
						this.getX() + i * 0.4f * MathHelper.cos((float) Math.toRadians(this.getRotationClient().y + 90)),
						this.getY() + 0.0625f,
						this.getZ() + i * 0.4f * MathHelper.sin((float) Math.toRadians(this.getRotationClient().y + 90)),
						this.getVelocity().getX() / 3f * Math.random(),
						Math.random() / 3,
						this.getVelocity().getZ() / 3f * Math.random()
				);
			}
			timer = this.getWorld().getBlockState(this.getBlockPos()).isOf(Blocks.POWERED_RAIL)?
					(int) (FPPClient.CONFIG.getSecPerPrint() * 3.33f):
					(int) (FPPClient.CONFIG.getSecPerPrint() * 6.66f);
		}
	}
}
