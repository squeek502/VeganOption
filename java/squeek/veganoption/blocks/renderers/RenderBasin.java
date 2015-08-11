package squeek.veganoption.blocks.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderBasin implements ISimpleBlockRenderingHandler
{
	public static int renderId;
	public static final TileEntityBasin dummyBasin = new TileEntityBasin();
	public static final double SIDE_WIDTH = BlockBasin.SIDE_WIDTH;

	public RenderBasin()
	{
		renderId = RenderingRegistry.getNextAvailableRenderId();
	}

	public void renderShared(Block block, int metadata, TileEntityBasin basin, RenderBlocks renderer)
	{
		renderBasin(block, metadata, basin, renderer);
	}

	public void renderBasin(Block block, int metadata, TileEntityBasin basin, RenderBlocks renderer)
	{
		this.renderSharedBlock(block, metadata, basin, renderer);

		int x = 0, y = 0, z = 0;
		Tessellator tessellator = Tessellator.instance;
		int brightness = 0, innerBrightness = 0;
		if (basin != dummyBasin)
		{
			x = basin.xCoord;
			y = basin.yCoord;
			z = basin.zCoord;

			int colorMultiplier = block.colorMultiplier(basin.getWorldObj(), x, y, z);
			float red = (float) (colorMultiplier >> 16 & 255) / 255.0F;
			float green = (float) (colorMultiplier >> 8 & 255) / 255.0F;
			float blue = (float) (colorMultiplier & 255) / 255.0F;

			if (EntityRenderer.anaglyphEnable)
			{
				float newRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
				float newGreen = (red * 30.0F + green * 70.0F) / 100.0F;
				float newBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
				red = newRed;
				green = newGreen;
				blue = newBlue;
			}

			brightness = block.getMixedBrightnessForBlock(basin.getWorldObj(), x, y, z);
			innerBrightness = (int) (basin.isClosed() ? brightness * 0.5F : brightness * 0.75F);
			tessellator.setBrightness(innerBrightness);
			tessellator.setColorOpaque_F(red, green, blue);
		}

		IIcon sideIcon = block.getBlockTextureFromSide(2);
		float offset = (float) SIDE_WIDTH;
		renderer.renderFaceXPos(block, (double) ((float) x - 1.0F + offset), (double) y, (double) z, sideIcon);
		renderer.renderFaceXNeg(block, (double) ((float) x + 1.0F - offset), (double) y, (double) z, sideIcon);
		renderer.renderFaceZPos(block, (double) x, (double) y, (double) ((float) z - 1.0F + offset), sideIcon);
		renderer.renderFaceZNeg(block, (double) x, (double) y, (double) ((float) z + 1.0F - offset), sideIcon);
		IIcon innerIconBottom = block.getBlockTextureFromSide(ForgeDirection.DOWN.ordinal());
		renderer.renderFaceYPos(block, (double) x, (double) ((float) y - 1.0F + offset), (double) z, innerIconBottom);

		if (basin.isClosed())
		{
			IIcon innerIconTop = block.getBlockTextureFromSide(ForgeDirection.UP.ordinal());
			renderer.renderFaceYNeg(block, (double) x, (double) ((float) y + 1.0F - offset), (double) z, innerIconTop);
		}

		FluidTankInfo tankInfo = basin.getTankInfo(ForgeDirection.UNKNOWN)[0];
		if (tankInfo.fluid != null && tankInfo.fluid.amount > 0)
		{
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			int color = tankInfo.fluid.getFluid().getColor(tankInfo.fluid);
			float red = (float) (color >> 16 & 255) / 255.0F;
			float green = (float) (color >> 8 & 255) / 255.0F;
			float blue = (float) (color & 255) / 255.0F;

			tessellator.setColorOpaque_F(red, green, blue);
			if (basin != dummyBasin)
			{
				tessellator.setBrightness(brightness);
			}

			IIcon fluidIcon = tankInfo.fluid.getFluid().getIcon();
			float percentFull = (float) tankInfo.fluid.amount / tankInfo.capacity;

			float fluidTopOffset = offset + (percentFull * (1.0F - offset - offset));
			renderer.setRenderBounds(offset, offset, offset, 1 - offset, fluidTopOffset, 1 - offset);

			renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, fluidIcon);
			renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, fluidIcon);
			renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, fluidIcon);
			renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, fluidIcon);
			renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, fluidIcon);

			GL11.glDisable(GL11.GL_LIGHTING);
		}
	}

	public void renderSharedBlock(Block block, int metadata, TileEntityBasin basin, RenderBlocks renderer)
	{
		if (basin == dummyBasin)
			renderStandardInventoryBlock(block, metadata, renderer);
		else
			renderer.renderStandardBlock(block, basin.xCoord, basin.yCoord, basin.zCoord);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		if (modelId != renderId)
			return;

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderStandardInventoryBlock(block, metadata, renderer);

		Tessellator tessellator = Tessellator.instance;
		
		IIcon sideIcon = block.getBlockTextureFromSide(2);
		IIcon innerIconBottom = block.getBlockTextureFromSide(ForgeDirection.DOWN.ordinal());
		float offset = (float) SIDE_WIDTH;

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, -1.0D + offset, 0.0D, 0.0D, sideIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 1.0D - offset, 0.0D, 0.0D, sideIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, -1.0D + offset, sideIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 1.0D - offset, sideIcon);
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, -1.0D + offset, 0.0D, innerIconBottom);
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (modelId != renderId)
			return false;

		renderShared(block, world.getBlockMetadata(x, y, z), (TileEntityBasin) world.getTileEntity(x, y, z), renderer);

		return true;
	}

	public void renderStandardInventoryBlock(Block block, int metadata, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
		tessellator.draw();
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return renderId;
	}

}
