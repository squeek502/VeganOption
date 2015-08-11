package squeek.veganoption.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import squeek.veganoption.helpers.BlockHelper.BlockPos;

public class FluidHelper
{
	public static final int FINITE_FLUID_MB_PER_META = (int) (0.125f * FluidContainerRegistry.BUCKET_VOLUME);

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

	public static FluidStack getFluidStackFromBlock(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if (block instanceof IFluidBlock)
		{
			return ((IFluidBlock) block).drain(world, x, y, z, false);
		}
		return getFluidStackFromBlock(block, world.getBlockMetadata(x, y, z));
	}

	public static FluidStack getFluidStackFromBlock(Block block, int metadata)
	{
		Fluid fluid = getFluidTypeOfBlock(block);
		if (fluid != null && metadata == getStillMetadata(fluid))
		{
			return new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
		}
		return null;
	}

	public static FluidStack consumeFluid(BlockPos blockPos, Fluid fluid, int maxAmount)
	{
		if (blockPos.world.isRemote)
			return null;

		if (maxAmount <= 0)
			return null;

		BlockHelper.BlockPos sourcePos = BlockHelper.followFluidStreamToSourceBlock(blockPos, fluid);

		if (sourcePos == null)
			return null;

		FluidStack fluidToAdd = FluidHelper.getFluidStackFromBlock(sourcePos.world, sourcePos.x, sourcePos.y, sourcePos.z);

		if (fluidToAdd == null || fluidToAdd.amount <= 0)
			return null;

		if (fluidToAdd.amount > maxAmount)
		{
			if (sourcePos.getBlock() instanceof BlockFluidFinite)
			{
				fluidToAdd = consumePartialFiniteFluidBlock(sourcePos, fluidToAdd, maxAmount);
			}
			else
				return null;
		}
		else
		{
			sourcePos.world.setBlockToAir(sourcePos.x, sourcePos.y, sourcePos.z);
		}

		return fluidToAdd;
	}

	public static FluidStack consumePartialFiniteFluidBlock(BlockPos fluidBlockPos, int maxAmount)
	{
		return consumePartialFiniteFluidBlock(fluidBlockPos, FluidHelper.getFluidStackFromBlock(fluidBlockPos.world, fluidBlockPos.x, fluidBlockPos.y, fluidBlockPos.z), maxAmount);
	}

	public static FluidStack consumePartialFiniteFluidBlock(BlockPos fluidBlockPos, FluidStack fullFluidStack, int maxAmount)
	{
		if (fluidBlockPos.world.isRemote)
			return null;

		int deltaMeta = -(maxAmount / FINITE_FLUID_MB_PER_META);
		int newMeta = fluidBlockPos.getMeta() + deltaMeta;

		if (deltaMeta == 0)
			return null;

		FluidStack fluidConsumed = fullFluidStack.copy();
		fluidConsumed.amount = Math.abs(deltaMeta) * FINITE_FLUID_MB_PER_META;

		if (newMeta >= 0)
			fluidBlockPos.world.setBlockMetadataWithNotify(fluidBlockPos.x, fluidBlockPos.y, fluidBlockPos.z, newMeta, 2);
		else
			fluidBlockPos.world.setBlockToAir(fluidBlockPos.x, fluidBlockPos.y, fluidBlockPos.z);

		System.out.println("set to " + newMeta);

		return fluidConsumed;
	}
}
