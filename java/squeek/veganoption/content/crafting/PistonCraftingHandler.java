package squeek.veganoption.content.crafting;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.veganoption.api.event.PistonEvent;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.helpers.FluidHelper;

import java.util.HashMap;

import static squeek.veganoption.helpers.FluidHelper.getStillFluidLevel;

public class PistonCraftingHandler
{
	// TODO: Persist displacedLiquids
	public static HashMap<WorldPosition, FluidStack> displacedLiquids = new HashMap<WorldPosition, FluidStack>();

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new PistonCraftingHandler());
	}

	public void saveDisplacedLiquidAt(WorldPosition pos)
	{
		displacedLiquids.remove(pos);

		IBlockState displacedState = pos.world.getBlockState(pos.pos);

		if (pos.world.isAirBlock(pos.pos))
			return;

		Fluid displacedFluid = FluidHelper.getFluidTypeOfBlock(displacedState);
		if (displacedFluid != null && FluidHelper.getFluidLevel(displacedState) == getStillFluidLevel(displacedFluid))
		{
			displacedLiquids.put(pos, new FluidStack(displacedFluid, Fluid.BUCKET_VOLUME));
		}
	}

	@SubscribeEvent
	public void onPistonTryExtend(PistonEvent.TryExtend event)
	{
		if (event.world.isRemote)
			return;

		if (event.world.getBlockState(event.basePos).getValue(BlockPistonBase.EXTENDED))
			return;

		BlockPos blockPos = event.headPos;
		WorldPosition pos = new WorldPosition(event.world, blockPos);

		saveDisplacedLiquidAt(pos);
	}

	@SubscribeEvent
	public void onPistonExtending(PistonEvent.Extending event)
	{
		if (event.world.isRemote)
			return;

		if (event.progress != 0.5F)
			return;

		for (PistonCraftingRecipe pistonRecipe : PistonCraftingRegistry.getRecipes())
		{
			if (pistonRecipe.tryCraft(event.world, event.headPos))
				break;
		}

		displacedLiquids.remove(new WorldPosition(event.world, event.headPos));
	}

	public static class WorldPosition
	{
		public final World world;
		public final BlockPos pos;

		public WorldPosition(World world, BlockPos pos)
		{
			this.pos = pos;
			this.world = world;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof WorldPosition)) return false;

			WorldPosition that = (WorldPosition) o;

			return world.equals(that.world) && pos.equals(that.pos);
		}

		@Override
		public int hashCode()
		{
			int result = world.hashCode();
			result = 31 * result + pos.hashCode();
			return result;
		}
	}
}
