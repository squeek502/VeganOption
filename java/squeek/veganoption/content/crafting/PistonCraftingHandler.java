package squeek.veganoption.content.crafting;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.api.event.PistonEvent;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.helpers.FluidHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
			if (pistonRecipe.tryCraft(event.world, event.headX, event.headY, event.headZ))
				return;
		}
	}

	@SubscribeEvent
	public void onPistonExtend(PistonEvent.TryExtend event)
	{
		if (event.world.isRemote)
			return;

		WorldPosition pos = new WorldPosition(event.world, event.headX, event.headY, event.headZ);
		displacedLiquids.remove(pos);

		Block displacedBlock = event.world.getBlock(event.headX, event.headY, event.headZ);

		if (displacedBlock == null || displacedBlock.isAir(event.world, event.headX, event.headY, event.headZ))
			return;

		Fluid displacedFluid = FluidHelper.getFluidTypeOfBlock(displacedBlock);
		int meta = event.world.getBlockMetadata(event.headX, event.headY, event.headZ);
		if (displacedFluid != null && meta == FluidHelper.getStillMetadata(displacedFluid))
		{
			displacedLiquids.put(pos, new FluidStack(displacedFluid, FluidContainerRegistry.BUCKET_VOLUME));
		}
	}

	@SubscribeEvent
	public void onPistonRetract(PistonEvent.Retract event)
	{
		if (event.world.isRemote)
			return;

		displacedLiquids.remove(new WorldPosition(event.world, event.headX, event.headY, event.headZ));
	}

	public static class WorldPosition extends ChunkPosition
	{
		public final World world;

		public WorldPosition(World world, int x, int y, int z)
		{
			super(x, y, z);
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
