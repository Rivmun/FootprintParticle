package com.rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

// Copy from net.minecraft.client.particle.PlayerCloudParticle
public class SnowDustParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    SnowDustParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet) {
        super(clientLevel, d, e, f, (double) 0.0F, (double) 0.0F, (double) 0.0F, spriteSet.first());
        this.friction = 0.96F;
        this.sprites = spriteSet;
        float j = 2.5F;
        this.xd *= (double) 0.1F;
        this.yd *= (double) 0.1F;
        this.zd *= (double) 0.1F;
        this.xd += g;
//        this.yd += h;
        this.zd += i;
        float k = 1.0F - this.random.nextFloat() * 0.3F;
        this.rCol = k;
        this.gCol = k;
        this.bCol = k;
        this.quadSize *= 1.875F;
        int l = (int) ((double) 8.0F / ((double) this.random.nextFloat() * 0.8 + 0.3));
        this.lifetime = (int) Math.max((float) l * 2.5F, 1.0F);
//        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed)
            this.setSpriteFromAge(this.sprites);
    }

    @Override
    public @NotNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleProvider<@NotNull SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public DefaultFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random) {
            Particle particle = new SnowDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            if (parameters instanceof SnowDustParticleType snowdust)
                particle.scale(snowdust.size);
            return particle;
        }
    }
}
