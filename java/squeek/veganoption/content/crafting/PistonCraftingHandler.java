package squeek.veganoption.content.crafting;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.veganoption.api.event.PistonEvent;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.helpers.FluidHelper;

public class PistonCraftingHandler
{
	// TODO: Persist displacedLiquids
	public static HashMap<WorldPosition, FluidStack> displacedLiquids = new HashMap<WorldPosition, FluidStack>();

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new PistonCraftingHandler());
	}

	@SubscribeEvent
	public void onCrushItem(PistonEvent.CrushItem event)
	{
		for (PistonCraftingRecipe pistonRecipe : PistonCraftingRegistry.getRecipes())
		{
			if (pistonRecipe.tryCraft(event.world, event.headPos))
				return;
		}
	}

	@SubscribeEvent
	public void onPistonExtend(PistonEvent.TryExtend event)
	{
		if (event.world.isRemote)
			return;

		BlockPos blockPos = event.headPos;
		WorldPosition pos = new WorldPosition(event.world, blockPos);
		displacedLiquids.remove(pos);

		IBlockState displacedState = event.world.getBlockState(blockPos);
		Block displacedBlock = displacedState.getBlock();

		if (displacedBlock == null || event.world.isAirBlock(blockPos))
			return;

		Fluid displacedFluid = FluidHelper.getFluidTypeOfBlock(displacedState);
		if (displacedFluid != null && FluidHelper.getStillFluidLevel(displacedFluid) == displacedState.getValue(BlockFluidBase.LEVEL))
		{
			displacedLiquids.put(pos, new FluidStack(displacedFluid, Fluid.BUCKET_VOLUME));
		}
	}

	@SubscribeEvent
	public void onPistonRetract(PistonEvent.Retract event)
	{
		if (event.world.isRemote)
			return;

		displacedLiquids.remove(new WorldPosition(event.world, event.headPos));
	}

	public static class WorldPosition extends ChunkPos
	{
		public final World world;

		public WorldPosition(World world, BlockPos pos)
		{
			super(pos);
			this.world = world;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((world == null) ? 0 : world.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (!super.equals(obj))
			{
				return false;
			}
			if (!(obj instanceof WorldPosition))
			{
				return false;
			}
			WorldPosition other = (WorldPosition) obj;
			if (world == null)
			{
				if (other.world != null)
				{
					return false;
				}
			}
			else if (!world.equals(other.world))
			{
				return false;
			}
			return true;
		}
	}
}
