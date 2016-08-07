package squeek.veganoption.blocks.renderers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.content.modules.Basin;

@SideOnly(Side.CLIENT)
public class RenderBasin extends TileEntitySpecialRenderer<TileEntityBasin>
{
	public static final TileEntityBasin dummyBasin = new TileEntityBasin();
	public static final double SIDE_WIDTH = BlockBasin.SIDE_WIDTH;

	@Override
	public void renderTileEntityAt(TileEntityBasin basin, double x, double y, double z, float partialTickTime, int destroyStage)
	{
		Block block = Basin.basin;
		int meta = basin == dummyBasin ? 0 : block.getMetaFromState(block.getDefaultState());
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(x, y, z);

		GlStateManager.disableLighting();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(x, y, z);

		World world = getWorld();
		int brightness = 0;
		if (basin != dummyBasin)
		{
			BlockPos pos = basin.getPos();
			IBlockState state = world.getBlockState(pos);
			int xInt = pos.getX();
			int yInt = pos.getY();
			int zInt = pos.getZ();

			// The default block color is -1, and BlockBasin does not use a color handler.
			int colorMultiplier = -1;
			float red = (colorMultiplier >> 16 & 255) / 255.0F;
			float green = (colorMultiplier >> 8 & 255) / 255.0F;
			float blue = (colorMultiplier & 255) / 255.0F;

			if (EntityRenderer.anaglyphEnable)
			{
				float newRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
				float newGreen = (red * 30.0F + green * 70.0F) / 100.0F;
				float newBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
				red = newRed;
				green = newGreen;
				blue = newBlue;
			}

			brightness = block.getPackedLightmapCoords(state, world, pos);
			int innerBrightness = (int) (basin.isClosed() ? brightness * 0.35F : brightness * 0.5F);
//			tessellator.setBrightness(innerBrightness);
			buffer.color(red, green, blue, 1);
		}

		IBlockState state = world.getBlockState(basin.getPos());
		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(model, state, 1F, false);
		
		buffer.finishDrawing();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		FluidTankInfo tankInfo = basin.getTankInfo(EnumFacing.NORTH)[0];
		if (tankInfo.fluid != null && tankInfo.fluid.amount > 0)
		{
			GlStateManager.enableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

			int color = tankInfo.fluid.getFluid().getColor(tankInfo.fluid);
			float red = (color >> 16 & 255) / 255.0F;
			float green = (color >> 8 & 255) / 255.0F;
			float blue = (color & 255) / 255.0F;

			buffer.color(red, green, blue, 1);
			if (basin != dummyBasin)
			{
//				tessellator.setBrightness(brightness);
			}

			ResourceLocation fluidRL = tankInfo.fluid.getFluid().getStill();
			TextureAtlasSprite fluidIcon = (TextureAtlasSprite) Minecraft.getMinecraft().getTextureManager().getTexture(fluidRL);
			float percentFull = (float) tankInfo.fluid.amount / tankInfo.capacity;

			float offset = (float) SIDE_WIDTH;
			float fluidTopOffset = offset + (percentFull * (1.0F - offset - offset));
			// render the liquid

			GlStateManager.disableLighting();
		}

		buffer.finishDrawing();

		GlStateManager.popMatrix();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableLighting();
	}
}
