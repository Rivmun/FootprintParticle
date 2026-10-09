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
//? if < 1.21.1 {
/^import net.minecraft.world.phys.Vec3;
//? if > 1.19.2
import org.joml.Vector3f;
^///? }
*///? }
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
//? if > 1.19.2 {
import org.joml.Quaternionf;
//? } else {
/*import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
*///? }
import org.jspecify.annotations.NonNull;

import java.util.List;

//~ if < 1.21.11 'SingleQuadParticle' -> 'TextureSheetParticle'
public class FootprintParticle extends SingleQuadParticle {
	protected float startAlpha;
	//~ if < 1.20.1 'Quaternionf' -> 'Quaternion'
	private final Quaternionf q;
	private final BlockPos pos;

	protected FootprintParticle(ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteProvider, FootprintParticleType parameters, String defName) {
		//~ if < 1.21.11 'vz, spriteProvider.get(RandomSource.create())' -> 'vz'
		super(clientWorld, x, y, z, vx, vy, vz, spriteProvider.get(RandomSource.create()));
		pos = new BlockPos(Mth.floor(this.x), Mth.floor(this.y - 0.02f), Mth.floor(this.z));

		this.xd = this.yd = this.zd = 0;
		this.setAlpha(Client.CONFIG.getFootprintAlpha());
		this.roll = (float) Mth.atan2(vx, vz);

		/*
		 * Quaternion expression powered by Deepseek.ai 👍
		 * rotating particle to horizontal plane and facing towards to entity moving direction
		 *
		 * <= 1.19.2 com.mojang.math.Quaternion 没有 rotateLocalZ，手工展开一次右乘绕面法线 Z 的 180° yaw：
		 * Hamilton (-sf, cf, -cf, -sf) * (0, 0, 1, 0) = (cf, sf, -sf, cf)，取同代表的 -q = (-cf, -sf, sf, -cf)。
		 * 这个额外旋转在本地平面上把贴图转 180°，与 >1.19.2 Quaternionf + 反向绕序 + U-swap 的 UV 修复叠加后，
		 * 两个分支的屏幕朝向完全一致；≤1.19.2 保持正向绕序与 vanilla 风格 UV，无需额外重映射。
		 */
		float halfAngle = this.roll / 2;
		double factor = Mth.SQRT_OF_TWO / 2;
		double sf = Mth.sin(halfAngle) * factor;
		double cf = Mth.cos(halfAngle) * factor;
		//? if > 1.19.2 {
		this.q = new Quaternionf(-cf, sf, sf, cf).rotateLocalY(Mth.PI);
		//? } else {
		/*this.q = new Quaternion((float) -cf, (float) -sf, (float) sf, (float) -cf);
		*///? }

		this.lifetime = (int) (Client.CONFIG.getPrintLifetime() * 20);
		this.quadSize = Client.CONFIG.getFootprintSize() * 0.03125f;

		this.quadSize *= Util.getEntityScale((parameters.entity));

		List<TextureAtlasSprite> spriteList = Util.getCustomSprites(parameters.entity, spriteProvider, defName);
		this.setSprite(spriteList.get((int) (Math.random() * spriteList.size())));
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
		//? if > 1.20.1 {
		this.renderRotatedQuad(vertexConsumer, camera, this.q, tickDelta);
		//? } else {
		/^Vec3 vec3 = camera.getPosition();
		float g = (float)(Mth.lerp(tickDelta, this.xo, this.x) - vec3.x());
		float h = (float)(Mth.lerp(tickDelta, this.yo, this.y) - vec3.y());
		float i = (float)(Mth.lerp(tickDelta, this.zo, this.z) - vec3.z());

		Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float j = this.getQuadSize(tickDelta);

		for(int k = 0; k < 4; ++k) {
			Vector3f vector3f = vector3fs[k];
			//~ if < 1.20.1 '.rotate' -> '.transform'
			vector3f.rotate(this.q);
			vector3f.mul(j);
			vector3f.add(g, h, i);
		}

		float l = this.getU0();
		float m = this.getU1();
		float n = this.getV0();
		float o = this.getV1();
		int p = this.getLightColor(tickDelta);

		// 难蚌 1.20.1 顶点要反向提交，否则粒子面会朝下渲染，然后被法线剔除。
		// 反向提交后贴图会左右颠倒，这里通过交换 U（l/m）把水平镜像翻正。
		// ≤1.19.2 使用正向提交（与 vanilla renderRotatedQuad 一致），朝向靠四元数保证。
		//? if > 1.19.2 {
		vertexConsumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(m, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		vertexConsumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(m, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		vertexConsumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(l, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		vertexConsumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(l, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		//? } else {
		/^¹vertexConsumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(m, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		vertexConsumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(m, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		vertexConsumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(l, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		vertexConsumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(l, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		¹^///? }
		^///? }
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
