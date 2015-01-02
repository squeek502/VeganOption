package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class BlockCompost extends Block
{
	// weird way to do this, but this will effectively make the direction
	// to check totally random, as this value is shared between all BlockCompost instances
	public int dirToCheck = 0;

	public BlockCompost()
	{
		super(Material.ground);
		this.setCreativeTab(CreativeTabs.tabBlock);
		setHarvestLevel("shovel", 0);
		setTickRandomly(true);
	}

	@Override
	// passive and very subtle soil building
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		super.updateTick(world, x, y, z, random);

		if (dirToCheck == 0)
			attemptSoilBuilding(world, x + 1, y, z, random, false);
		else if (dirToCheck == 1)
			attemptSoilBuilding(world, x - 1, y, z, random, false);
		else if (dirToCheck == 2)
			attemptSoilBuilding(world, x, y, z + 1, random, false);
		else if (dirToCheck == 3)
			attemptSoilBuilding(world, x, y, z - 1, random, false);
		else if (dirToCheck == 4)
			attemptSoilBuilding(world, x, y + 1, z, random, true);

		dirToCheck = (dirToCheck + 1) % 5;
	}

	public void attemptSoilBuilding(World world, int x, int y, int z, Random random, boolean growPlantDirectly)
	{
		Block block = world.getBlock(x, y, z);
		if (block == Blocks.farmland)
		{
			Block blockToHelpGrow = world.getBlock(x, y + 1, z);
			blockToHelpGrow.updateTick(world, x, y + 1, z, random);
		}
		else if (growPlantDirectly && block instanceof IPlantable)
		{
			block.updateTick(world, x, y, z, random);
		}
	}
}
