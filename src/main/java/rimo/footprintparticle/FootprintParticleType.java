package rimo.footprintparticle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

public class FootprintParticleType extends DefaultParticleType {
	
	public String entityID;

	protected FootprintParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}
	
	public ParticleEffect setData(String id) {
		this.entityID = id;
		return this;
	}
	
}
