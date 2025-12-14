package com.rimo.footprintparticle.particle;

import com.rimo.footprintparticle.FPPClient;
import com.rimo.footprintparticle.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.List;

public class FootprintParticle extends SingleQuadParticle {
	protected float startAlpha;
	private final Quaternionf q;
	private final BlockPos pos;

	protected FootprintParticle(ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteProvider, FootprintParticleType parameters, String defName) {
		super(clientWorld, x, y, z, vx, vy, vz, spriteProvider.get(RandomSource.create()));
		pos = new BlockPos(Mth.floor(this.x), Mth.floor(this.y - 0.02f), Mth.floor(this.z));

		this.setParticleSpeed(0, 0, 0);
		this.setAlpha(FPPClient.CONFIG.getFootprintAlpha());
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

		this.lifetime = (int) (FPPClient.CONFIG.getPrintLifetime() * 20);
		this.quadSize = FPPClient.CONFIG.getFootprintSize() * 0.03125f;

		this.quadSize *= Util.getEntityScale((parameters.entity));

		try {
			List<TextureAtlasSprite> spriteList = Util.getCustomSprites(parameters.entity, spriteProvider, defName);
			this.setSprite(spriteList.get((int) (Math.random() * spriteList.size())));
		} catch (Exception e) {
			FPPClient.LOGGER.error("Wrong custom texture for " + EntityType.getKey(parameters.entity.getType()) + ", please check.");
		}
	}

    @Override
    protected @NotNull Layer getLayer() {
        return Layer.TRANSLUCENT;
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

		if (this.level.isRaining() && this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos) <= this.y)
			if (this.age + FPPClient.CONFIG.getLifeTimeAcc() < this.lifetime)
				this.age += FPPClient.CONFIG.getLifeTimeAcc();

		if (this.age++ >= this.lifetime || this.level.isEmptyBlock(pos))
			this.remove();

		if (this.age > this.lifetime / 2f)
			this.alpha = this.startAlpha - (this.startAlpha * (this.age - this.lifetime / 2f) / (this.lifetime / 2f));
	}

	@Override
	public void extract(@NotNull QuadParticleRenderState renderState, @NotNull Camera camera, float tickDelta) {
		this.extractRotatedQuad(renderState, camera, this.q, tickDelta);
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleProvider<@NotNull SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, @NotNull RandomSource random) {
			return new FootprintParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, (FootprintParticleType) parameters, "footprint");
		}
	}

}
