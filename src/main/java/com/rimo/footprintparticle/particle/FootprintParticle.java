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
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import org.joml.Quaternionf;

import java.util.List;

public class FootprintParticle extends SpriteBillboardParticle {
	protected float startAlpha;
	private final Quaternionf q;
	private final BlockPos pos;

	protected FootprintParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider, FootprintParticleType parameters, String defName) {
		super(clientWorld, x, y, z, vx, vy, vz);
		pos = new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.y - 0.02f), MathHelper.floor(this.z));

		this.setVelocity(0, 0, 0);
		this.setAlpha(FPPClient.CONFIG.getFootprintAlpha());
		this.angle = (float) MathHelper.atan2(vx, vz);

		/*
		 * Quaternion expression powered by Deepseek.ai 👍
		 * rotating particle to horizontal plane and facing towards to entity moving direction
		 */
		float halfAngle = this.angle / 2;
		double factor = MathHelper.SQUARE_ROOT_OF_TWO / 2;
		double sf = MathHelper.sin(halfAngle) * factor;
		double cf = MathHelper.cos(halfAngle) * factor;
		this.q = new Quaternionf(-cf, sf, sf, cf).rotateLocalY(MathHelper.PI);

		this.maxAge = (int) (FPPClient.CONFIG.getPrintLifetime() * 20);
		this.scale = FPPClient.CONFIG.getFootprintSize() * 0.03125f;

		this.scale *= Util.getEntityScale((parameters.entity));

		try {
			List<Sprite> spriteList = Util.getCustomSprites(parameters.entity, spriteProvider, defName);
			this.setSprite(spriteList.get((int) (Math.random() * spriteList.size())));
		} catch (Exception e) {
			FPPClient.LOGGER.error("Wrong custom texture for " + EntityType.getId(parameters.entity.getType()).toString() + ", please check.");
			this.setSprite(spriteProvider);
		}
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

		if (this.world.isRaining() && this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() <= this.y)
			this.age += FPPClient.CONFIG.getLifeTimeAcc();

		if (this.age++ >= this.maxAge || this.world.isAir(pos))
			this.markDead();
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		this.method_60373(vertexConsumer, camera, this.q, tickDelta);
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new FootprintParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, (FootprintParticleType) parameters, "footprint");
		}
	}

}
