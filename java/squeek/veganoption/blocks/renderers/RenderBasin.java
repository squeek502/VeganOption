package squeek.veganoption.blocks.renderers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.helpers.RenderHelper;

@SideOnly(Side.CLIENT)
public class RenderBasin extends TileEntitySpecialRenderer<TileEntityBasin>
{
	public static final double SIDE_WIDTH = BlockBasin.SIDE_WIDTH;

	@Override
	public void renderTileEntityAt(TileEntityBasin basin, double x, double y, double z, float partialTickTime, int destroyStage)
	{
		FluidTankInfo tankInfo = basin.getTankInfo(EnumFacing.NORTH)[0];
		if (tankInfo.fluid == null || tankInfo.fluid.amount <= 0)
			return;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		int brightness = getWorld().getCombinedLight(basin.getPos(), tankInfo.fluid.getFluid().getLuminosity());
		float percentFull = (float) tankInfo.fluid.amount / tankInfo.capacity;

		double fluidTop = SIDE_WIDTH + (percentFull * (1.0F - SIDE_WIDTH - SIDE_WIDTH));
		AxisAlignedBB bounds = new AxisAlignedBB(SIDE_WIDTH, SIDE_WIDTH, SIDE_WIDTH, 1.0D - SIDE_WIDTH, fluidTop, 1.0D - SIDE_WIDTH);

		// render the liquid
		RenderHelper.putStillFluidCube(tankInfo.fluid, bounds, brightness, buffer);

		tessellator.draw();

		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableLighting();
	}
}
