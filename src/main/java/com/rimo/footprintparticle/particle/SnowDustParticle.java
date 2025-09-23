package com.rimo.footprintparticle.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

public class SnowDustParticle extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    // Copy from net.minecraft.client.particle.CloudParticle
    SnowDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, (double)0.0F, (double)0.0F, (double)0.0F, spriteProvider.getFirst());
        this.velocityMultiplier = 0.96F;
        this.spriteProvider = spriteProvider;
        float f = 2.5F;
        this.velocityX *= (double)0.1F;
        this.velocityY *= (double)0.1F;
        this.velocityZ *= (double)0.1F;
        this.velocityX += velocityX;
//        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        float g = 1.0F - (float)(Math.random() * (double)0.3F);
        this.red = g;
        this.green = g;
        this.blue = g;
        this.scale *= 1.875F;
        int i = (int)((double)8.0F / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
//        this.collidesWithWorld = false;
        this.updateSprite(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.dead)
            this.updateSprite(this.spriteProvider);
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.field_62641;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
            Particle particle = new SnowDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            if (parameters instanceof SnowDustParticleType snowdust)
                particle.scale(snowdust.size);
            return particle;
        }
    }
}
