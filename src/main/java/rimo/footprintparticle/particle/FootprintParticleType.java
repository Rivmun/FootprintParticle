package rimo.footprintparticle.particle;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

public class FootprintParticleType extends DefaultParticleType {

	public LivingEntity entity;

	public FootprintParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}

	public ParticleEffect setData(LivingEntity entity) {
		this.entity = entity;
		return this;
	}
	
}
