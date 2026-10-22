package com.rimo.footprintparticle.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.Collections;
import java.util.List;

public class FootprintParticleType extends SimpleParticleType {

	public String entityId = "";
	public List<String> tags = Collections.emptyList();

	public FootprintParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}

	public ParticleOptions setEntityInfo(String entityId, List<String> tags) {
		this.entityId = entityId;
		this.tags = tags;
		return this;
	}

}
