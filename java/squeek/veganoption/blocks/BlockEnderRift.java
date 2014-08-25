package squeek.veganoption.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import squeek.veganoption.VeganOption;
import squeek.veganoption.registry.Content;

public class BlockEnderRift extends BlockEndPortal
{
	public static class MaterialEnderRift extends MaterialLogic
	{
		public MaterialEnderRift()
		{
			super(MapColor.airColor);
			this.setImmovableMobility();
		}
	}

	public BlockEnderRift()
	{
		super(new MaterialEnderRift());
	}

	public boolean onFluidFlowInto(World world, int x, int y, int z, int flowDecay)
	{
		Block fluidBlock = world.getBlock(x, y + 1, z);
		if (fluidBlock == Blocks.flowing_water || fluidBlock == Blocks.water && flowDecay == 9)
		{
		}

		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block blockChanged)
	{
		if (blockChanged == Blocks.air)
		{
			boolean isWaterAbove = world.getBlock(x, y + 1, z) == Blocks.water || world.getBlock(x, y + 1, z) == Blocks.flowing_water;
			if (isWaterAbove && world.getBlock(x, y - 1, z).isAir(world, x, y - 1, z))
			{
				world.setBlock(x, y - 1, z, Content.rawEnder, 7, 3);
				VeganOption.Log.info("onNeighborBlockChange " + blockChanged);
			}
		}
		super.onNeighborBlockChange(world, x, y, z, blockChanged);
	}
	
	// stop from teleporting to the end
	@Override
	public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
	{
		return;
	}
}
