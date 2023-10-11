package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.FPPClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class FootprintParticle extends SpriteBillboardParticle {
	public final SpriteProvider spriteProvider;
	protected float startAlpha;

	protected FootprintParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider) {
		super(clientWorld, x, y, z, vx, vy, vz);

		this.spriteProvider = spriteProvider;
		this.setSprite(spriteProvider.getSprite(random));

		this.setVelocity(0, 0, 0);
		this.setAlpha(FPPClient.CONFIG.getFootprintAlpha());
		this.angle = (float) MathHelper.atan2(vx, vz);
		this.maxAge = (int) (FPPClient.CONFIG.getPrintLifetime() * 20);
		this.scale = FPPClient.CONFIG.getFootprintSize();
	}

	@Override
	public void setAlpha(float a) {
		super.setAlpha(a);
		this.startAlpha = a;
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
			this.alpha -= this.startAlpha / this.maxAge * 2;

		if (this.age++ >= this.maxAge || this.world.isAir(new BlockPos(this.x, this.y - 0.02f, this.z)))
			this.markDead();
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		var camPos = camera.getPos();
		var x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.getX());
		var y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.getY());
		var z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.getZ());
		Vec3f[] pos = new Vec3f[]{new Vec3f(-1, 0, -1), new Vec3f(-1, 0, 1), new Vec3f(1, 0, 1), new Vec3f(1, 0, -1)};		//TODO: why cannot use vec3d here?

		for (int k = 0; k < 4; ++k) {
			/*
			 * In Minecraft,
			 * rotate a vec3f point P around a axis with θ radian (anticlockwise)
			 * needs a vec3f(x, y, z).normalize() direct to this axis direction
			 * then simply call P.rotate(new Quaternion(sinθ*x, sinθ*y, sinθ*z, cosθ)) with half the θ
			 * Oh, MAGIC! (👈 he is totally idiot.)
			 *
			 * here we need vertex rotate around Y axis, so leave X and Z for zero.
			 */
			pos[k].rotate(new Quaternion(
					0,
					MathHelper.sin(this.angle / 2),
					0,
					MathHelper.cos(this.angle / 2)
			));
			pos[k].scale(this.getSize(tickDelta));
			pos[k].add(x, y, z);
		}

		vertexConsumer.vertex(pos[0].getX(), pos[0].getY(), pos[0].getZ()).texture(this.getMaxU(), this.getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta)).next();
		vertexConsumer.vertex(pos[1].getX(), pos[1].getY(), pos[1].getZ()).texture(this.getMaxU(), this.getMinV()).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta)).next();
		vertexConsumer.vertex(pos[2].getX(), pos[2].getY(), pos[2].getZ()).texture(this.getMinU(), this.getMinV()).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta)).next();
		vertexConsumer.vertex(pos[3].getX(), pos[3].getY(), pos[3].getZ()).texture(this.getMinU(), this.getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(this.getBrightness(tickDelta)).next();
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
			if (parameters instanceof FootprintParticleType footprintParameters)
				particle.scale *= FPPClient.getEntityScale((footprintParameters.entity));
			return particle;
		}
	}

}
