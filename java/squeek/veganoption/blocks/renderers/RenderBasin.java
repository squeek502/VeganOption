package squeek.veganoption.blocks.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.content.modules.Basin;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBasin extends TileEntitySpecialRenderer implements IItemRenderer
{
	public static final TileEntityBasin dummyBasin = new TileEntityBasin();
	public static final double SIDE_WIDTH = BlockBasin.SIDE_WIDTH;

	public void renderTileEntityAt(TileEntityBasin basin, double x, double y, double z, float partialTickTime)
	{
		RenderBlocks renderer = new RenderBlocks(basin.getWorldObj());
		
		Block block = Basin.basin;
		int meta = basin == dummyBasin ? 0 : basin.blockMetadata;
		bindTexture(TextureMap.locationBlocksTexture);
		renderer.setRenderBoundsFromBlock(block);

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslated(x, y, z);

		if (basin == dummyBasin)
		{
			GL11.glEnable(GL11.GL_LIGHTING);
			renderStandardInventoryBlock(block, meta, renderer);
			GL11.glDisable(GL11.GL_LIGHTING);
		}
		
		GL11.glDisable(GL11.GL_LIGHTING);

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();

		int brightness = 0, innerBrightness = 0;
		if (basin != dummyBasin)
		{
			int xInt = basin.xCoord;
			int yInt = basin.yCoord;
			int zInt = basin.zCoord;

			int colorMultiplier = block.colorMultiplier(basin.getWorldObj(), xInt, yInt, zInt);
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

			brightness = block.getMixedBrightnessForBlock(basin.getWorldObj(), xInt, yInt, zInt);
			innerBrightness = (int) (basin.isClosed() ? brightness * 0.35F : brightness * 0.5F);
			tessellator.setBrightness(innerBrightness);
			tessellator.setColorOpaque_F(red, green, blue);
		}

		IIcon sideIcon = block.getBlockTextureFromSide(2);
		float offset = (float) SIDE_WIDTH;
		renderer.renderFaceXPos(block, -1.0F + offset, 0, 0, sideIcon);
		renderer.renderFaceXNeg(block, 1.0F - offset, 0, 0, sideIcon);
		renderer.renderFaceZPos(block, 0, 0, -1.0F + offset, sideIcon);
		renderer.renderFaceZNeg(block, 0, 0, 1.0F - offset, sideIcon);
		IIcon innerIconBottom = block.getBlockTextureFromSide(ForgeDirection.DOWN.ordinal());
		renderer.renderFaceYPos(block, 0, -1.0F + offset, 0, innerIconBottom);

		if (basin.isClosed())
		{
			IIcon innerIconTop = block.getBlockTextureFromSide(ForgeDirection.UP.ordinal());
			renderer.renderFaceYNeg(block, 0, 1.0F - offset, 0, innerIconTop);
		}
		
		tessellator.draw();
		tessellator.startDrawingQuads();

		FluidTankInfo tankInfo = basin.getTankInfo(ForgeDirection.UNKNOWN)[0];
		if (tankInfo.fluid != null && tankInfo.fluid.amount > 0)
		{
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			int color = tankInfo.fluid.getFluid().getColor(tankInfo.fluid);
			float red = (color >> 16 & 255) / 255.0F;
			float green = (color >> 8 & 255) / 255.0F;
			float blue = (color & 255) / 255.0F;

			tessellator.setColorOpaque_F(red, green, blue);
			if (basin != dummyBasin)
			{
				tessellator.setBrightness(brightness);
			}

			IIcon fluidIcon = tankInfo.fluid.getFluid().getIcon();
			float percentFull = (float) tankInfo.fluid.amount / tankInfo.capacity;

			float fluidTopOffset = offset + (percentFull * (1.0F - offset - offset));
			renderer.setRenderBounds(offset, offset, offset, 1 - offset, fluidTopOffset, 1 - offset);

			renderer.renderFaceYPos(block, 0, 0, 0, fluidIcon);
			renderer.renderFaceXNeg(block, 0, 0, 0, fluidIcon);
			renderer.renderFaceXPos(block, 0, 0, 0, fluidIcon);
			renderer.renderFaceZNeg(block, 0, 0, 0, fluidIcon);
			renderer.renderFaceZPos(block, 0, 0, 0, fluidIcon);

			GL11.glDisable(GL11.GL_LIGHTING);
		}

		tessellator.draw();

		GL11.glPopMatrix();
		GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
	{
		this.renderTileEntityAt((TileEntityBasin) p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		switch (type)
		{
			case ENTITY:
			{
				renderTileEntityAt(dummyBasin, -0.5D, -0.5D, -0.5D, 0.0F);
				return;
			}
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
			{
				renderTileEntityAt(dummyBasin, 0.0F, 0.0F, 0.0F, 0.0F);
				return;
			}
			case INVENTORY:
			{
				renderTileEntityAt(dummyBasin, 0.5F, 0.3F, 0.5F, 0.0F);
				return;
			}
			default:
				return;
		}
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

}
