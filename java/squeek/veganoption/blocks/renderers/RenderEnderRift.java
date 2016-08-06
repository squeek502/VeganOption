package squeek.veganoption.blocks.renderers;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;

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
		float entityX = (float) this.rendererDispatcher.entityX;
		float entityY = (float) this.rendererDispatcher.entityY;
		float entityZ = (float) this.rendererDispatcher.entityZ;
		GlStateManager.disableLighting();
		RANDOM.setSeed(31100L);
		float topOffset = 0.75F;
		float bottomOffset = 0.25F;

		// Top face
		for (int i = 0; i < 2; ++i)
		{
			GlStateManager.pushMatrix();
			float f5 = i == 0 ? 16 : 1;
			float f6 = 0.0625F;

			if (i == 0)
			{
				bindTexture(END_SKY_TEXTURE);
				f5 = 65.0F;
				f6 = 0.125F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}

			if (i == 1)
			{
				bindTexture(END_PORTAL_TEXTURE);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				f6 = 0.5F;
			}

			float f8 = (float) (-(y + topOffset));
			float f9 = f8 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f10 = f8 + f5 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f11 = f9 / f10;
			f11 += (float) (y + topOffset);
			GlStateManager.translate(entityX, f11, entityZ);
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(1F, 0F, 0F, 0F));
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
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.rotate((i * i * 4321 + i * 9) * 2F, 0, 0, 1);
			GlStateManager.translate(-0.5F, -0.5F, 0.0F);
			GlStateManager.translate(-entityX, -entityZ, -entityY);
			f9 = f8 + (float) ActiveRenderInfo.getPosition().yCoord;
			GlStateManager.translate(ActiveRenderInfo.getPosition().xCoord * f5 / f9, ActiveRenderInfo.getPosition().zCoord * f5 / f9, -entityY);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			f11 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f6;
			float f12 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f6;
			float f13 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f6;
			vertexbuffer.pos(x, y + 0.75D, z).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x, y + 0.75D, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x + 1.0D, y + 0.75D, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x + 1.0D, y + 0.75D, z).color(f11, f12, f13, 1.0F).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			bindTexture(END_SKY_TEXTURE);
		}

		// Bottom face
		GL11.glCullFace(GL11.GL_FRONT);
		for (int i = 0; i < 2; ++i)
		{
			GL11.glPushMatrix();
			float f5 = i == 0 ? 16 : 1;
			float f6 = 1 - 0.0625F;

			if (i == 0)
			{
				bindTexture(END_SKY_TEXTURE);
				f5 = -65.0F;
				f6 = 0.125F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if (i == 1)
			{
				bindTexture(END_PORTAL_TEXTURE);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				f6 = 0.5F;
			}

			float f8 = (float) (y + bottomOffset);
			float f9 = f8 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f10 = f8 + f5 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f11 = f9 / f10;
			f11 += (float) (y + bottomOffset);
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
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.rotate((i * i * 4321 + i * 9) * 2f, 0, 0, 1);
			GlStateManager.translate(-0.5F, -0.5F, 0.0F);
			GlStateManager.translate(-entityX, -entityZ, -entityY);
			f9 = f8 + (float) ActiveRenderInfo.getPosition().yCoord;
			GlStateManager.translate(ActiveRenderInfo.getPosition().xCoord * f5 / f9, ActiveRenderInfo.getPosition().zCoord * f5 / f9, -entityY);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			f11 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f6;
			float f12 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f6;
			float f13 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f6;
			vertexbuffer.pos(x, y + 0.75D, z).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x, y + 0.75D, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x + 1.0D, y + 0.75D, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x + 1.0D, y + 0.75D, z).color(f11, f12, f13, 1.0F).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			bindTexture(END_SKY_TEXTURE);

		}
		GlStateManager.disableBlend();
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
		GlStateManager.enableLighting();
	}

	private FloatBuffer getBuffer(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_)
	{
		buffer.clear();
		buffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
		buffer.flip();
		return buffer;
	}
}