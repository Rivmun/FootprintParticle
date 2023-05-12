package rimo.footprintparticle;

import org.joml.Quaternionf;
import org.joml.Vector3f;

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
import net.minecraft.entity.EntityType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FootprintParticle extends SpriteBillboardParticle{
	public final SpriteProvider spriteProvider;

	protected FootprintParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider) {
		super(clientWorld, x, y, z, vx, vy, vz);

		this.spriteProvider = spriteProvider;
		this.setSprite(spriteProvider.getSprite(random));

		this.setVelocity(0, 0, 0);
		//this.angle = (float) Math.atan2(-vx, vz);		// TODO: this angle change has no effect yet.

		this.maxAge = (int) (FPPClient.CONFIG.getPrintLifetime() * 20);
		this.scale = 0.5f;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.y -= 0.01f / this.maxAge;
		this.prevPosY = this.y;

		if (this.age > this.maxAge / 2)
			this.setAlpha((float) Math.cos((((float) this.age - (float) this.maxAge / 2) / (float) this.maxAge) * Math.PI));

		if (this.age++ >= this.maxAge || this.world.isAir(new BlockPos(this.x, this.y - 0.02f, this.z)))
			this.markDead();
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d vec3d = camera.getPos();
		float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

		Vector3f[] Vec3fs = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
		float j = this.getSize(tickDelta);

		for (int k = 0; k < 4; ++k) {
			Vector3f Vec3f2 = Vec3fs[k];
			Vec3f2.rotate(new Quaternionf(0f, -0.7f, 0.7f, 0f));
			Vec3f2.mul(j);
			Vec3f2.add(f, g, h);
		}

		float minU = this.getMinU();
		float maxU = this.getMaxU();
		float minV = this.getMinV();
		float maxV = this.getMaxV();
		int l = this.getBrightness(tickDelta);

		vertexConsumer.vertex(Vec3fs[0].x, Vec3fs[0].y, Vec3fs[0].z).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
		vertexConsumer.vertex(Vec3fs[1].x, Vec3fs[1].y, Vec3fs[1].z).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
		vertexConsumer.vertex(Vec3fs[2].x, Vec3fs[2].y, Vec3fs[2].z).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
		vertexConsumer.vertex(Vec3fs[3].x, Vec3fs[3].y, Vec3fs[3].z).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(l).next();
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
			if (parameters instanceof FootprintParticleType footprintParameters) {
				try {
					for (String str : FPPClient.CONFIG.getSizePerMob()) {
						String[] str2 = str.split(",");
						if (str2[0].contentEquals(EntityType.getId(footprintParameters.entity.getType()).toString())) {
							particle.scale = Float.parseFloat(str2[1]);
						}
					}
				} catch (Exception e) {
					// Ignore...
				}
				if (footprintParameters.entity.isBaby()) {
					particle.scale *= 0.66f;
				}
			}
			return particle;
		}
	}
}
