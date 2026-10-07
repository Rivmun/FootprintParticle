package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.Client;
import com.rimo.footprintparticle.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
//? if > 1.21.1 {
//~ if < 26.1 '.level.QuadParticleRenderState' -> '.QuadParticleRenderState'
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.RandomSource;
//? } else {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
*///? }
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

import java.util.List;

//~ if < 1.21.11 'SingleQuadParticle' -> 'TextureSheetParticle'
public class FootprintParticle extends SingleQuadParticle {
	protected float startAlpha;
	private final Quaternionf q;
	private final BlockPos pos;

	protected FootprintParticle(ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteProvider, FootprintParticleType parameters, String defName) {
		//~ if < 1.21.11 'vz, spriteProvider.get(RandomSource.create())' -> 'vz'
		super(clientWorld, x, y, z, vx, vy, vz, spriteProvider.get(RandomSource.create()));
		pos = new BlockPos(Mth.floor(this.x), Mth.floor(this.y - 0.02f), Mth.floor(this.z));

		this.setParticleSpeed(0, 0, 0);
		this.setAlpha(Client.CONFIG.getFootprintAlpha());
		this.roll = (float) Mth.atan2(vx, vz);

		/*
		 * Quaternion expression powered by Deepseek.ai 👍
		 * rotating particle to horizontal plane and facing towards to entity moving direction
		 */
		float halfAngle = this.roll / 2;
		double factor = Mth.SQRT_OF_TWO / 2;
		double sf = Mth.sin(halfAngle) * factor;
		double cf = Mth.cos(halfAngle) * factor;
		this.q = new Quaternionf(-cf, sf, sf, cf).rotateLocalY(Mth.PI);

		this.lifetime = (int) (Client.CONFIG.getPrintLifetime() * 20);
		this.quadSize = Client.CONFIG.getFootprintSize() * 0.03125f;

		this.quadSize *= Util.getEntityScale((parameters.entity));

		try {
			List<TextureAtlasSprite> spriteList = Util.getCustomSprites(parameters.entity, spriteProvider, defName);
			this.setSprite(spriteList.get((int) (Math.random() * spriteList.size())));
		} catch (Exception e) {
			Client.LOGGER.error("Wrong custom texture for {}, please check.", EntityType.getKey(parameters.entity.getType()));
		}
	}

    @Override
	public void setAlpha(float a) {
		super.setAlpha(a);
		this.startAlpha = a;
	}

    @Override
	public void tick() {
		this.y -= 0.01f / this.lifetime;
		this.yo = this.y;

		//~ if < 1.21.11 ', pos' -> ', pos.getX(), pos.getZ()'
		if (this.level.isRaining() && this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos) <= this.y)
			if (this.age + Client.CONFIG.getLifeTimeAcc() < this.lifetime)
				this.age += Client.CONFIG.getLifeTimeAcc();

		if (this.age++ >= this.lifetime || this.level.isEmptyBlock(pos))
			this.remove();

		if (this.age > this.lifetime / 2f)
			this.alpha = this.startAlpha - (this.startAlpha * (this.age - this.lifetime / 2f) / (this.lifetime / 2f));
	}

	//? if > 1.21.1 {
	@Override
	protected @NonNull Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	@Override
	public void extract(@NonNull QuadParticleRenderState renderState, @NonNull Camera camera, float tickDelta) {
		this.extractRotatedQuad(renderState, camera, this.q, tickDelta);
	}
	//? } else {
	/*@Override
	public @NonNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void render(@NonNull VertexConsumer vertexConsumer, @NonNull Camera camera, float tickDelta) {
		this.renderRotatedQuad(vertexConsumer, camera, this.q, tickDelta);
	}
	*///? }

	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		//~ if < 1.21.11 'velocityZ, @NonNull RandomSource random' -> 'velocityZ'
		public Particle createParticle(@NonNull SimpleParticleType parameters, @NonNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, @NonNull RandomSource random) {
			return new FootprintParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, (FootprintParticleType) parameters, "footprint");
		}
	}

}
