package com.rimo.footprintparticle.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

public class SnowDustParticleType extends DefaultParticleType {
	public float size;

	public SnowDustParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}

	public ParticleEffect setData(float size) {
		this.size = size;
		return this;
	}
}
