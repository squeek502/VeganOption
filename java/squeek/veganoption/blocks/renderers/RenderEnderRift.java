package squeek.veganoption.blocks.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;

import java.nio.FloatBuffer;
import java.util.Random;

// modified version of RenderEndPortal
// draws the bottom face, uses much fewer passes
@SideOnly(Side.CLIENT)
public class RenderEnderRift extends TileEntitySpecialRenderer<TileEntityEnderRift>
{
	private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);
	private FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

	@Override
	public void renderTileEntityAt(TileEntityEnderRift tile, double x, double y, double z, float partialTicks, int destroyStage)
	{
		GlStateManager.disableLighting();

		renderFace(EnumFaceDirection.UP, x, y, z, 3, 31103L);
		renderFace(EnumFaceDirection.DOWN, x, y, z, 3, 31103L);

		GlStateManager.disableBlend();
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
		GlStateManager.enableLighting();
	}

	private void renderFace(EnumFaceDirection face, double x, double y, double z, int numIterations, long seed)
	{
		RANDOM.setSeed(seed);
		double faceOffset = face == EnumFaceDirection.UP ? 0.75D : 0.25D;
		float entityX = (float) this.rendererDispatcher.entityX;
		float entityY = (float) this.rendererDispatcher.entityY;
		float entityZ = (float) this.rendererDispatcher.entityZ;

		for (int i = 0; i < numIterations; ++i)
		{
			GlStateManager.pushMatrix();
			float f5 = i + numIterations;
			float f6 = 0.0625F * i;
			float f7 = 0.25F + ((float) i / numIterations) / 2.0F;

			if (i == 0)
			{
				bindTexture(END_SKY_TEXTURE);
				f7 = 0.1F;
				f5 = face == EnumFaceDirection.UP ? 65.0F : -65.0F;
				f6 = 0.125F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}

			if (i >= 1)
			{
				bindTexture(END_PORTAL_TEXTURE);
			}

			if (i == 1)
			{
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				f6 = 0.5f;
			}

			if (face == EnumFaceDirection.DOWN)
				f6 *= 2f;

			float f8 = (float) (face == EnumFaceDirection.UP ? (-(y + faceOffset)) : (y + faceOffset));
			float f9 = f8 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f10 = f8 + f5 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f11 = f9 / f10;
			f11 += (float) (y + faceOffset);
			GlStateManager.translate(entityX, f11, entityZ);
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
			GlStateManager.popMatrix();

			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();

			GlStateManager.translate(0.0F, Minecraft.getSystemTime() % 700000L / 700000.0F, 0.0F);
			GlStateManager.scale(f6, f6, f6);
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
			GlStateManager.rotate((float) (i * i * 4321 * i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-0.5F, -0.5F, 0.0F);
			GlStateManager.translate(-entityX, -entityZ, -entityY);
			f9 = f8 + (float) ActiveRenderInfo.getPosition().yCoord;
			GlStateManager.translate(ActiveRenderInfo.getPosition().xCoord * f5 / f9, ActiveRenderInfo.getPosition().zCoord * f5 / f9, -entityY);

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			f11 = (RANDOM.nextFloat() * 0.5F + 0.1F);
			float f12 = (RANDOM.nextFloat() * 0.5F + 0.4F);
			float f13 = (RANDOM.nextFloat() * 0.5F + 0.5F);

			f11 *= f7;
			f12 *= f7;
			f13 *= f7;

			if (face == EnumFaceDirection.UP)
			{
				vertexbuffer.pos(x, y + faceOffset, z).color(f11, f12, f13, 1.0F).endVertex();
				vertexbuffer.pos(x, y + faceOffset, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
				vertexbuffer.pos(x + 1.0D, y + faceOffset, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
				vertexbuffer.pos(x + 1.0D, y + faceOffset, z).color(f11, f12, f13, 1.0F).endVertex();
			}
			else
			{
				vertexbuffer.pos(x, y + faceOffset, z).color(f11, f12, f13, 1.0F).endVertex();
				vertexbuffer.pos(x + 1.0D, y + faceOffset, z).color(f11, f12, f13, 1.0F).endVertex();
				vertexbuffer.pos(x + 1.0D, y + faceOffset, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
				vertexbuffer.pos(x, y + faceOffset, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
			}

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			bindTexture(END_SKY_TEXTURE);
		}

	}

	private FloatBuffer getBuffer(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_)
	{
		buffer.clear();
		buffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
		buffer.flip();
		return buffer;
	}
}