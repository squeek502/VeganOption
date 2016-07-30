package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCompost extends Block
{
	public BlockCompost()
	{
		super(Material.ground);
		setHarvestLevel("shovel", 0);
		setTickRandomly(true);
		setSoundType(SoundType.GROUND);
	}

	@Override
	// passive and very subtle soil building
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		super.updateTick(world, x, y, z, random);

		// skip ForgeDirection.DOWN
		ForgeDirection randomDirection = ForgeDirection.getOrientation(random.nextInt(ForgeDirection.VALID_DIRECTIONS.length - 1) + 1);

		x += randomDirection.offsetX;
		y += randomDirection.offsetY;
		z += randomDirection.offsetZ;

		attemptSoilBuilding(world, x, y, z, random, randomDirection == ForgeDirection.UP);
	}

	public static boolean tryGrowthTickAt(World world, int x, int y, int z, Random random)
	{
		Block block = world.getBlock(x, y, z);
		if ((block instanceof IPlantable || block instanceof IGrowable) && block.getTickRandomly())
		{
			block.updateTick(world, x, y, z, random);
			return true;
		}
		return false;
	}

	public void attemptSoilBuilding(World world, int x, int y, int z, Random random, boolean growPlantDirectly)
	{
		tryGrowthTickAt(world, x, y, z, random);

		if (growPlantDirectly)
			tryGrowthTickAt(world, x, y + 1, z, random);
	}
}
