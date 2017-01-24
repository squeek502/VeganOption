package squeek.veganoption.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

import static squeek.veganoption.helpers.RandomHelper.random;

public class BlockCompost extends Block
{
	public BlockCompost()
	{
		super(Material.GROUND);
		setHarvestLevel("shovel", 0);
		setTickRandomly(true);
		setSoundType(SoundType.GROUND);
	}

	@Override
	// passive and very subtle soil building
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		super.updateTick(world, pos, state, rand);

		// skip ForgeDirection.DOWN
		EnumFacing randomDirection = EnumFacing.DOWN;
		while (randomDirection == EnumFacing.DOWN)
		{
			randomDirection = EnumFacing.random(rand);
		}

		attemptSoilBuilding(world, pos.offset(randomDirection), random, randomDirection == EnumFacing.UP);
	}

	public static boolean tryGrowthTickAt(World world, BlockPos pos, Random random)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if ((block instanceof IPlantable || block instanceof IGrowable) && block.getTickRandomly())
		{
			block.updateTick(world, pos, state, random);
			return true;
		}
		return false;
	}

	public void attemptSoilBuilding(World world, BlockPos pos, Random random, boolean growPlantDirectly)
	{
		tryGrowthTickAt(world, pos, random);

		if (growPlantDirectly)
			tryGrowthTickAt(world, pos.up(), random);
	}
}
