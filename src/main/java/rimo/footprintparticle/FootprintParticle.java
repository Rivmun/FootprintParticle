package rimo.footprintparticle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class FootprintParticle extends SpriteBillboardParticle{
	public final SpriteProvider spriteProvider;

	protected FootprintParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider) {
		super(clientWorld, x, y, z, vx, vy, vz);
		
		this.angle = (float) Math.atan2(-vx, vz);		// TODO: this angle change has no effect yet.

        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;

        this.spriteProvider = spriteProvider;
        this.setSprite(spriteProvider.getSprite(random));
		
        this.maxAge = (int) (FPPClient.CONFIG.getPrintLifetime() * 20);
        this.scale = 0.5f;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age > this.maxAge / 2)
        	this.setAlpha((float) Math.cos((((float) this.age - (float) this.maxAge / 2) / (float) this.maxAge) * Math.PI));
        if (this.age++ >= this.maxAge)
            this.markDead();
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

        Vec3f[] Vec3fs = new Vec3f[]{new Vec3f(-1.0f, -1.0f, 0.0f), new Vec3f(-1.0f, 1.0f, 0.0f), new Vec3f(1.0f, 1.0f, 0.0f), new Vec3f(1.0f, -1.0f, 0.0f)};
        float j = this.getSize(tickDelta);

        for (int k = 0; k < 4; ++k) {
            Vec3f Vec3f2 = Vec3fs[k];
            Vec3f2.rotate(new Quaternion(0f, -0.7f, 0.7f, 0f));
            Vec3f2.scale(j);
            Vec3f2.add(f, g, h);
        }

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int l = this.getBrightness(tickDelta);

        vertexConsumer.vertex(Vec3fs[0].getX(), Vec3fs[0].getY(), Vec3fs[0].getZ()).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[1].getX(), Vec3fs[1].getY(), Vec3fs[1].getZ()).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[2].getX(), Vec3fs[2].getY(), Vec3fs[2].getZ()).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[3].getX(), Vec3fs[3].getY(), Vec3fs[3].getZ()).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        	FootprintParticle particle = new FootprintParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        	if (parameters instanceof FootprintParticleType footprintParameters && footprintParameters.entityID != null) {
                for (String str : FPPClient.CONFIG.getSizePerMob()) {
                	String[] str2 = str.split(",");
                	if (str2[0].contentEquals(footprintParameters.entityID) && str2[1] != null) {
                		particle.scale = Float.parseFloat(str2[1]);
                	}
                }
        	}
            return particle;
        }
    }

}
