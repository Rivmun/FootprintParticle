package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;

import java.util.List;

public class FootprintParticle extends SpriteBillboardParticle {
	protected float startAlpha;
	private final BlockPos pos;

	protected FootprintParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider, FootprintParticleType parameters, String defName) {
		super(clientWorld, x, y, z, vx, vy, vz);
		pos = new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.y - 0.02f), MathHelper.floor(this.z));

		this.velocityX = 0;
		this.velocityY = 0;
		this.velocityZ = 0;
		this.setColorAlpha(FPPClient.CONFIG.getFootprintAlpha());
		this.angle = (float) MathHelper.atan2(vx, vz);
		this.maxAge = (int) (FPPClient.CONFIG.getPrintLifetime() * 20);
		this.scale = FPPClient.CONFIG.getFootprintSize() * 0.03125f;

		this.scale *= Util.getEntityScale((parameters.entity));

		List<Sprite> spriteList = Util.getCustomSprites(parameters.entity, spriteProvider, defName);
		try {
			this.setSprite(spriteList.get((int) (Math.random() * spriteList.size())));
		} catch (Exception e) {
			FPPClient.LOGGER.error("Wrong custom texture for " + EntityType.getId(parameters.entity.getType()).toString() + ", please check.");
			this.setSprite(spriteProvider);
		}
	}

	@Override
	public void setColorAlpha(float a) {
		super.setColorAlpha(a);
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

		if (this.world.isRaining() && this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() <= this.y)
			if (this.age + FPPClient.CONFIG.getLifeTimeAcc() < this.maxAge)
				this.age += FPPClient.CONFIG.getLifeTimeAcc();

		if (this.age++ >= this.maxAge || this.world.isAir(pos))
			this.markDead();

		if (this.age > this.maxAge / 2f)
			this.colorAlpha = this.startAlpha - (this.startAlpha * (this.age - this.maxAge / 2f) / (this.maxAge / 2f));
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d camPos = camera.getPos();
		float x = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.getX());
		float y = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.getY());
		float z = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.getZ());
		/*
		 * In Minecraft,
		 * rotate a vec3f point P around an axis with θ radian (anticlockwise)
		 * needs a NORMALIZED normal vec3f(x, y, z) direct to this axis direction
		 * then simply call P.rotate(new Quaternion(sinθ*x, sinθ*y, sinθ*z, cosθ)) with half the θ
		 * Oh, MAGIC! (👈 he is totally idiot.)
		 *
		 * here we need vertex rotate around Y axis, so the normal is (0, 1, 0), that's let X and Z are 0.
		 */
		Quaternion q = new Quaternion(
				0,
				MathHelper.sin(this.angle / 2),
				0,
				MathHelper.cos(this.angle / 2)
		);
		Vec3f[] pos = new Vec3f[]{new Vec3f(-1, 0, -1), new Vec3f(-1, 0, 1), new Vec3f(1, 0, 1), new Vec3f(1, 0, -1)};
		float i = this.getSize(tickDelta);

		for (int j = 0; j < 4; ++j) {
			Vec3f vec3f = pos[j];
			vec3f.rotate(q);
			vec3f.scale(i);
			vec3f.add(x, y, z);
		}

		float k = this.getMinU();
		float l = this.getMaxU();
		float n = this.getMaxV();
		float m = this.getMinV();
		int o = this.getBrightness(tickDelta);
		vertexConsumer.vertex(pos[0].getX(), pos[0].getY(), pos[0].getZ()).texture(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();
		vertexConsumer.vertex(pos[1].getX(), pos[1].getY(), pos[1].getZ()).texture(l, m).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();
		vertexConsumer.vertex(pos[2].getX(), pos[2].getY(), pos[2].getZ()).texture(k, m).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();
		vertexConsumer.vertex(pos[3].getX(), pos[3].getY(), pos[3].getZ()).texture(k, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new FootprintParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, (FootprintParticleType) parameters, "footprint");
		}
	}

}
