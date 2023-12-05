package squeek.veganoption.content.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import squeek.veganoption.api.event.PistonEvent;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.helpers.FluidHelper;

import java.util.HashMap;
import java.util.Map;

public class PistonCraftingHandler
{
	// TODO: Persist displacedLiquids
	public static Map<WorldPosition, FluidStack> displacedLiquids = new HashMap<>();

	public static void init()
	{
		NeoForge.EVENT_BUS.register(new PistonCraftingHandler());
	}

	public void saveDisplacedLiquidAt(WorldPosition pos)
	{
		displacedLiquids.remove(pos);

		BlockState displacedState = pos.level.getBlockState(pos.pos);

		if (displacedState.isAir())
			return;

		Fluid displacedFluid = FluidHelper.getFluidTypeOfBlock(displacedState);
		if (displacedFluid != null && displacedFluid.isSource(displacedState.getFluidState()))
		{
			displacedLiquids.put(pos, new FluidStack(displacedFluid, FluidType.BUCKET_VOLUME));
		}
	}

	@SubscribeEvent
	public void onPistonTryExtend(PistonEvent.TryExtend event)
	{
		if (event.level.isClientSide())
			return;

		if (event.level.getBlockState(event.basePos).getValue(PistonBaseBlock.EXTENDED))
			return;

		BlockPos blockPos = event.headPos;
		WorldPosition pos = new WorldPosition(event.level, blockPos);

		saveDisplacedLiquidAt(pos);
	}

	@SubscribeEvent
	public void onPistonExtending(PistonEvent.Extending event)
	{
		if (event.level.isClientSide())
			return;

		if (event.progress != 0.5F)
			return;

		for (PistonCraftingRecipe pistonRecipe : PistonCraftingRegistry.getRecipes())
		{
			if (pistonRecipe.tryCraft(event.level, event.headPos))
				break;
		}

		displacedLiquids.remove(new WorldPosition(event.level, event.headPos));
	}

	public static class WorldPosition
	{
		public final Level level;
		public final BlockPos pos;

		public WorldPosition(Level level, BlockPos pos)
		{
			this.pos = pos;
			this.level = level;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof WorldPosition that)) return false;
			return this.level.equals(that.level) && pos.equals(that.pos);
		}

		@Override
		public int hashCode()
		{
			int result = level.hashCode();
			result = 31 * result + pos.hashCode();
			return result;
		}
	}
}
