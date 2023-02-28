package rimo.footprintparticle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

public class FootprintParticleType extends DefaultParticleType {
	
	public String entityID;
	public int lightLevel;

	protected FootprintParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}
	
	public ParticleEffect setData(String id, int lit) {
		this.entityID = id;
		this.lightLevel = lit;
		return this;
	}
	
}
