package squeek.veganoption.blocks.renderers;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.helpers.RenderHelper;

@OnlyIn(Dist.CLIENT)
public class RenderBasin implements BlockEntityRenderer<TileEntityBasin>
{
	public static final double SIDE_WIDTH = BlockBasin.SIDE_WIDTH;

	@Override
	public void render(TileEntityBasin basin, float partialTickTime, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		FluidTank tank = basin.fluidTank;
		if (tank.isEmpty())
			return;
		poseStack.pushPose();
		Tesselator tess = Tesselator.getInstance();
		BufferBuilder builder = tess.getBuilder();
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

		int brightness = basin.getLevel().getRawBrightness(basin.getBlockPos(), tank.getFluid().getFluid().getFluidType().getLightLevel());
		float percentFull = (float) tank.getFluidAmount() / tank.getCapacity();
		double fluidTop = SIDE_WIDTH + (percentFull * (1f - SIDE_WIDTH - SIDE_WIDTH));
		AABB bounds = new AABB(SIDE_WIDTH, SIDE_WIDTH, SIDE_WIDTH, 1d - SIDE_WIDTH, fluidTop, 1d - SIDE_WIDTH);

		RenderHelper.putStillFluidCube(tank.getFluid(), bounds, brightness, buffer.getBuffer(RenderType.translucent()));

		tess.end();
		poseStack.popPose();
	}
}
