package rimo.footprintparticle.mixin;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	public abstract boolean isLiving();
	
	@Inject(method = "onLanding", at = @At("TAIL"), cancellable = true)
	public void onLanding(CallbackInfo ci) {
		if (this.isLiving()) {
			LivingEntityMixin entity = (LivingEntityMixin) (Object) this;
			entity.footprintGenerator();
		}
	}
}
