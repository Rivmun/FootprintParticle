package com.rimo.footprintparticle.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;

public class FootprintParticleType extends SimpleParticleType {

	public LivingEntity entity;

	public FootprintParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}

	public ParticleOptions setData(LivingEntity entity) {
		this.entity = entity;
		return this;
	}

}
