package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.BlockHelper;

public class BlockEncrustedObsidian extends Block
{
	public BlockEncrustedObsidian()
	{
		super(BlockBehaviour.Properties.of()
			.sound(SoundType.STONE)
			.strength(50f, 2000f)
			.mapColor(MapColor.COLOR_BLACK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops());
	}

	public static void tryPlacePortalAdjacentTo(Level level, BlockPos pos)
	{
		for (BlockPos blockPosToCheck : BlockHelper.getBlocksAdjacentTo(pos))
		{
			BlockState stateToCheck = level.getBlockState(blockPosToCheck);
			Block blockToCheck = stateToCheck.getBlock();
			if (blockToCheck != Ender.enderRift.get() && BlockEnderRift.isValidPortalLocation(level, blockPosToCheck) && stateToCheck.canBeReplaced())
			{
				level.setBlockAndUpdate(blockPosToCheck, Ender.enderRift.get().defaultBlockState());
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	{
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
		tryPlacePortalAdjacentTo(level, pos);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston)
	{
		super.onPlace(state, level, pos, state, movedByPiston);
		tryPlacePortalAdjacentTo(level, pos);
	}
}
