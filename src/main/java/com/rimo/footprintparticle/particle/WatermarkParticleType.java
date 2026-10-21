package com.rimo.footprintparticle.particle;

public class WatermarkParticleType extends FootprintParticleType {
	int startAlpha;

	public WatermarkParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}

	public WatermarkParticleType setStartAlpha(int startAlpha) {
		this.startAlpha = startAlpha;
		return this;
	}
}
