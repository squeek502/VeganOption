package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.IPlantable;

public class BlockCompost extends Block
{
	public BlockCompost()
	{
		super(BlockBehaviour.Properties.of()
			.strength(0.5f)
			.randomTicks()
			.sound(SoundType.GRASS)
			.requiresCorrectToolForDrops()
			.mapColor(MapColor.DIRT));
	}

	@Override
	// passive and very subtle soil building
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
	{
		super.randomTick(state, level, pos, rand);

		// skip ForgeDirection.DOWN
		Direction randomDirection = Direction.DOWN;
		while (randomDirection == Direction.DOWN)
		{
			randomDirection = Direction.getRandom(rand);
		}

		attemptSoilBuilding(level, pos.relative(randomDirection), rand, randomDirection == Direction.UP);
	}

	public static boolean tryGrowthTickAt(ServerLevel level, BlockPos pos, RandomSource random)
	{
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		if ((block instanceof IPlantable || block instanceof BonemealableBlock) && block.isRandomlyTicking(state))
		{
			block.randomTick(state, level, pos, random);
			return true;
		}
		return false;
	}

	public void attemptSoilBuilding(ServerLevel level, BlockPos pos, RandomSource random, boolean growPlantDirectly)
	{
		tryGrowthTickAt(level, pos.above(), random);

		if (growPlantDirectly)
			tryGrowthTickAt(level, pos, random);
	}
}
