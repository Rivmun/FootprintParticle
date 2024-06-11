package com.rimo.footprintparticle.particle;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;

public class FootprintParticleType extends SimpleParticleType {

	public LivingEntity entity;

	public FootprintParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}

	public ParticleEffect setData(LivingEntity entity) {
		this.entity = entity;
		return this;
	}

}
