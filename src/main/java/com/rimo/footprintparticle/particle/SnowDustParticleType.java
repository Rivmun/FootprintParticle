package com.rimo.footprintparticle.particle;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;

public class SnowDustParticleType extends SimpleParticleType {
    public float size;

    public SnowDustParticleType(boolean alwaysShow) {
        super(alwaysShow);
    }

    public ParticleEffect setData(float size) {
        this.size = size;
        return this;
    }
}
