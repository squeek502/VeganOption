package squeek.veganoption.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidHelper
{
	// source amount is 8, bucket volume / 8 = 125
	public static final int MB_PER_LEVEL = (int) (0.125f * FluidType.BUCKET_VOLUME);

	// based on balance from existing mods, is sort of close to vanilla (vanilla: bottle = 333.333 mB)
	public static final int BOTTLES_PER_BUCKET = 4;
	public static final int MB_PER_BOTTLE = FluidType.BUCKET_VOLUME / BOTTLES_PER_BUCKET;

	public static ItemStack toItemStack(Fluid fluid)
	{
		if (fluid == null || fluid.defaultFluidState().createLegacyBlock().getBlock() == Blocks.AIR)
			return null;

		return new ItemStack(fluid.defaultFluidState().createLegacyBlock().getBlock().asItem());
	}

	public static ItemStack toItemStack(FluidStack fluidStack)
	{
		if (fluidStack == FluidStack.EMPTY)
			return ItemStack.EMPTY;

		return FluidHelper.toItemStack(fluidStack.getFluid());
	}

	@Nonnull
	public static FluidStack fromItemStack(ItemStack itemStack)
	{
		if (itemStack.isEmpty() || Block.byItem(itemStack.getItem()) == Blocks.AIR)
			return FluidStack.EMPTY;

		Block block = Block.byItem(itemStack.getItem());
		Fluid fluid = getFluidTypeOfBlock(block.defaultBlockState());

		return fluid != null ? new FluidStack(fluid, FluidType.BUCKET_VOLUME) : FluidStack.EMPTY;
	}

	public static Fluid getFluidTypeOfBlock(BlockState state)
	{
		FluidState fluidState = state.getFluidState();
		return fluidState.isEmpty() ? null : fluidState.getType();
	}

	public static int getFluidLevel(Level level, BlockPos pos)
	{
		return getFluidLevel(level.getBlockState(pos));
	}

	public static int getFluidLevel(BlockState state)
	{
		return state.getValue(LiquidBlock.LEVEL);
	}

	public static FluidStack getFluidStackFromBlock(Level level, BlockPos pos)
	{
		return getFluidStackFromBlock(level.getBlockState(pos));
	}

	public static FluidStack getFluidStackFromBlock(BlockState state)
	{
		Fluid fluid = getFluidTypeOfBlock(state);
		if (fluid != null && state.getFluidState().isSource())
		{
			return new FluidStack(fluid, FluidType.BUCKET_VOLUME);
		}
		return null;
	}

	public static FluidStack consumeExactFluid(Level level, BlockPos blockPos, Fluid fluid, int amount)
	{
		return consumeFluid(level, blockPos, fluid, amount, amount);
	}

	public static FluidStack consumeFluid(Level level, BlockPos blockPos, Fluid fluid, int maxAmount)
	{
		FluidStack consumed = consumeFluid(level, blockPos, fluid, 0, maxAmount);
		return consumed != null && consumed.getAmount() > 0 ? consumed : null;
	}

	public static FluidStack consumeFluid(Level level, BlockPos blockPos, Fluid fluid, int minAmount, int maxAmount)
	{
		if (EffectiveSide.get().isClient())
			return null;

		if (maxAmount < minAmount)
			return null;

		IFluidHandler fluidHandler = getFluidHandlerAt(level, blockPos, Direction.UP);
		if (fluidHandler != null)
		{
			FluidStack stackDrained = fluidHandler.drain(new FluidStack(fluid, maxAmount), IFluidHandler.FluidAction.SIMULATE);
			if (stackDrained != null && stackDrained.getAmount() >= minAmount)
				return fluidHandler.drain(stackDrained, IFluidHandler.FluidAction.EXECUTE);
		}

		BlockPos sourcePos = BlockHelper.followFluidStreamToSourceBlock(level, blockPos, fluid);

		if (sourcePos == null)
			return null;

		FluidStack fluidToAdd = FluidHelper.getFluidStackFromBlock(level, sourcePos);

		if (fluidToAdd == null)
			return null;

		if (fluidToAdd.getAmount() > maxAmount)
		{
			fluidToAdd = consumePartialFiniteFluidBlock(level, sourcePos, fluidToAdd, maxAmount);
			return fluidToAdd;
		}

		if (fluidToAdd.getAmount() >= minAmount && fluidToAdd.getAmount() <= maxAmount)
		{
			BlockHelper.setBlockToAir(level, sourcePos);
			return fluidToAdd;
		}

		return null;
	}

	public static FluidStack consumePartialFiniteFluidBlock(Level level, BlockPos fluidBlockPos, int maxAmount)
	{
		return consumePartialFiniteFluidBlock(level, fluidBlockPos, FluidHelper.getFluidStackFromBlock(level, fluidBlockPos), maxAmount);
	}

	public static FluidStack consumePartialFiniteFluidBlock(Level level, BlockPos fluidBlockPos, FluidStack fullFluidStack, int maxAmount)
	{
		if (level.isClientSide())
			return null;

		int deltaMeta = -(maxAmount / MB_PER_LEVEL);
		int newMeta = getFluidLevel(level, fluidBlockPos) + deltaMeta;

		if (deltaMeta == 0)
			return null;

		FluidStack fluidConsumed = fullFluidStack.copy();
		fluidConsumed.setAmount(Math.abs(deltaMeta) * MB_PER_LEVEL);

		if (newMeta >= 0)
			level.setBlockAndUpdate(fluidBlockPos, level.getBlockState(fluidBlockPos).setValue(FlowingFluid.LEVEL, newMeta));
		else
			BlockHelper.setBlockToAir(level, fluidBlockPos);

		return fluidConsumed;
	}

	public static IFluidHandler getFluidHandlerAt(Level level, BlockPos pos, Direction facing)
	{
		BlockEntity tile = level.getBlockEntity(pos);

		if (tile instanceof IFluidHandler)
			return (IFluidHandler) tile;

		if (tile != null)
			return tile.getCapability(Capabilities.FLUID_HANDLER, facing).orElse(null);

		return null;
	}
}
