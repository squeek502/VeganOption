package squeek.veganoption.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidHelper
{
	public static ItemStack toItemStack(FluidStack fluidStack)
	{
		if (fluidStack == null || fluidStack.getFluid() == null || fluidStack.getFluid().getBlock() == null)
			return null;

		return new ItemStack(fluidStack.getFluid().getBlock());
	}

	public static FluidStack fromItemStack(ItemStack itemStack)
	{
		if (itemStack == null || itemStack.getItem() == null || Block.getBlockFromItem(itemStack.getItem()) == null)
			return null;

		Block block = Block.getBlockFromItem(itemStack.getItem());
		Fluid fluid = getFluidTypeOfBlock(block);

		return fluid != null ? new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME) : null;
	}

	public static boolean isBlockMaterialWater(Block block)
	{
		return block != null && block.getMaterial() == Material.water;
	}

	public static boolean isBlockMaterialLava(Block block)
	{
		return block != null && block.getMaterial() == Material.lava;
	}

	public static Fluid getFluidTypeOfBlock(Block block)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);

		if (fluid != null)
			return fluid;
		else if (isBlockMaterialWater(block))
			return FluidRegistry.WATER;
		else if (isBlockMaterialLava(block))
			return FluidRegistry.LAVA;

		return null;
	}

	public static int getStillMetadata(Fluid fluid)
	{
		if (fluid != null && fluid.getBlock() instanceof BlockFluidFinite)
			return 7;
		else
			return 0;
	}
}
