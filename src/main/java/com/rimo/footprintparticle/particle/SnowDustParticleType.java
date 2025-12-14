package com.rimo.footprintparticle.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class SnowDustParticleType extends SimpleParticleType {
    public float size;

    public SnowDustParticleType(boolean alwaysShow) {
        super(alwaysShow);
    }

    public ParticleOptions setData(float size) {
        this.size = size;
        return this;
    }
}
