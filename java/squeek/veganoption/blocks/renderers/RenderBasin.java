package squeek.veganoption.blocks.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
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

		int fluidLight = tank.getFluid().getFluid().getFluidType().getLightLevel();
		int luminosity = Math.max((packedLight >> 4) & 0xF, fluidLight);
		int brightness = (packedLight & 0xF00000) | luminosity << 4;

		float percentFull = (float) tank.getFluidAmount() / tank.getCapacity();
		double fluidTop = SIDE_WIDTH + (percentFull * (1f - SIDE_WIDTH - SIDE_WIDTH));
		AABB bounds = new AABB(SIDE_WIDTH, SIDE_WIDTH, SIDE_WIDTH, 1d - SIDE_WIDTH, fluidTop, 1d - SIDE_WIDTH);

		RenderHelper.putStillFluidCube(tank.getFluid(), bounds, brightness, buffer.getBuffer(RenderType.translucent()), poseStack);

		poseStack.popPose();
	}
}
