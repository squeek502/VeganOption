package squeek.veganoption.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidHelper
{
	public static final int FINITE_FLUID_MB_PER_META = (int) (0.125f * Fluid.BUCKET_VOLUME);

	public static ItemStack toItemStack(Fluid fluid)
	{
		if (fluid == null || fluid.getBlock() == null)
			return null;

		return new ItemStack(fluid.getBlock());
	}

	public static ItemStack toItemStack(FluidStack fluidStack)
	{
		if (fluidStack == null)
			return null;

		return FluidHelper.toItemStack(fluidStack.getFluid());
	}

	public static FluidStack fromItemStack(ItemStack itemStack)
	{
		if (itemStack.isEmpty() || Block.getBlockFromItem(itemStack.getItem()) == Blocks.AIR)
			return null;

		Block block = Block.getBlockFromItem(itemStack.getItem());
		Fluid fluid = getFluidTypeOfBlock(block.getDefaultState());

		return fluid != null ? new FluidStack(fluid, Fluid.BUCKET_VOLUME) : null;
	}

	public static boolean isBlockMaterialWater(IBlockState state)
	{
		return state.getBlock() != null && state.getMaterial() == Material.WATER;
	}

	public static boolean isBlockMaterialLava(IBlockState state)
	{
		return state.getBlock() != null && state.getMaterial() == Material.LAVA;
	}

	public static Fluid getFluidTypeOfBlock(IBlockState state)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(state.getBlock());

		if (fluid != null)
			return fluid;
		else if (isBlockMaterialWater(state))
			return FluidRegistry.WATER;
		else if (isBlockMaterialLava(state))
			return FluidRegistry.LAVA;

		return null;
	}

	public static int getStillFluidLevel(Fluid fluid)
	{
		if (fluid != null)
			return getStillFluidLevel(fluid.getBlock());
		else
			return 0;
	}

	public static int getStillFluidLevel(Block block)
	{
		return block instanceof BlockFluidBase ? ((BlockFluidBase) block).getMaxRenderHeightMeta() : 0;
	}

	public static int getFluidLevel(World world, BlockPos pos)
	{
		return getFluidLevel(world.getBlockState(pos));
	}

	public static int getFluidLevel(IBlockState state)
	{
		if (state.getBlock() instanceof BlockFluidBase)
			return state.getValue(BlockFluidBase.LEVEL);
		else if (state.getBlock() instanceof BlockLiquid)
			return state.getValue(BlockLiquid.LEVEL);
		else
			return 0;
	}

	public static FluidStack getFluidStackFromBlock(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof IFluidBlock)
		{
			return ((IFluidBlock) state.getBlock()).drain(world, pos, false);
		}
		return getFluidStackFromBlock(state);
	}

	public static FluidStack getFluidStackFromBlock(IBlockState state)
	{
		Fluid fluid = getFluidTypeOfBlock(state);
		if (fluid != null && getFluidLevel(state) == getStillFluidLevel(fluid))
		{
			return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
		}
		return null;
	}

	public static FluidStack consumeExactFluid(World world, BlockPos blockPos, Fluid fluid, int amount)
	{
		return consumeFluid(world, blockPos, fluid, amount, amount);
	}

	public static FluidStack consumeFluid(World world, BlockPos blockPos, Fluid fluid, int maxAmount)
	{
		FluidStack consumed = consumeFluid(world, blockPos, fluid, 0, maxAmount);
		return consumed != null && consumed.amount > 0 ? consumed : null;
	}

	public static FluidStack consumeFluid(World world, BlockPos blockPos, Fluid fluid, int minAmount, int maxAmount)
	{
		if (world.isRemote)
			return null;

		if (maxAmount < minAmount)
			return null;

		net.minecraftforge.fluids.capability.IFluidHandler fluidHandler = getFluidHandlerAt(world, blockPos, EnumFacing.UP);
		if (fluidHandler != null)
		{
			FluidStack stackDrained = fluidHandler.drain(new FluidStack(fluid, maxAmount), false);
			if (stackDrained != null && stackDrained.amount >= minAmount)
				return fluidHandler.drain(stackDrained, true);
		}

		BlockPos sourcePos = BlockHelper.followFluidStreamToSourceBlock(world, blockPos, fluid);

		if (sourcePos == null)
			return null;

		FluidStack fluidToAdd = FluidHelper.getFluidStackFromBlock(world, sourcePos);

		if (fluidToAdd == null)
			return null;

		if (fluidToAdd.amount > maxAmount)
		{
			if (world.getBlockState(sourcePos).getBlock() instanceof BlockFluidFinite)
			{
				fluidToAdd = consumePartialFiniteFluidBlock(world, sourcePos, fluidToAdd, maxAmount);
				return fluidToAdd;
			}
			else
				return null;
		}

		if (fluidToAdd.amount >= minAmount && fluidToAdd.amount <= maxAmount)
		{
			world.setBlockToAir(sourcePos);
			return fluidToAdd;
		}

		return null;
	}

	public static FluidStack consumePartialFiniteFluidBlock(World world, BlockPos fluidBlockPos, int maxAmount)
	{
		return consumePartialFiniteFluidBlock(world, fluidBlockPos, FluidHelper.getFluidStackFromBlock(world, fluidBlockPos), maxAmount);
	}

	public static FluidStack consumePartialFiniteFluidBlock(World world, BlockPos fluidBlockPos, FluidStack fullFluidStack, int maxAmount)
	{
		if (world.isRemote)
			return null;

		int deltaMeta = -(maxAmount / FINITE_FLUID_MB_PER_META);
		int newMeta = getFluidLevel(world, fluidBlockPos) + deltaMeta;

		if (deltaMeta == 0)
			return null;

		FluidStack fluidConsumed = fullFluidStack.copy();
		fluidConsumed.amount = Math.abs(deltaMeta) * FINITE_FLUID_MB_PER_META;

		if (newMeta >= 0)
			world.setBlockState(fluidBlockPos, world.getBlockState(fluidBlockPos).withProperty(BlockFluidBase.LEVEL, newMeta), 2);
		else
			world.setBlockToAir(fluidBlockPos);

		return fluidConsumed;
	}

	public static IFluidHandler getFluidHandlerAt(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		TileEntity tile = world.getTileEntity(pos);

		if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
			return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);

		return null;
	}
}
