package squeek.veganoption.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import squeek.veganoption.content.modules.Ender;

import java.util.ArrayList;
import java.util.List;

public class BlockRawEnder extends LiquidBlock
{
	public static final BooleanProperty IS_SOURCE = BooleanProperty.create("is_source");
	private final List<FluidState> sourceStatesCache;
	private final List<FluidState> flowingStatesCache;
	private boolean fluidStateCachesInitialized = false;

	public BlockRawEnder()
	{
		super(() -> (FlowingFluid) Ender.rawEnderStill.get(), BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_BLACK)
			.replaceable()
			.noCollission()
			.randomTicks()
			.strength(100f)
			.lightLevel(state -> 3)
			.pushReaction(PushReaction.DESTROY)
			.noLootTable()
			.liquid()
			.sound(SoundType.EMPTY));
		registerDefaultState(getStateDefinition().any().setValue(IS_SOURCE, true).setValue(LEVEL, 0));
		sourceStatesCache = new ArrayList<>();
		flowingStatesCache = new ArrayList<>();
		for (int i = 0; i < 16; i++)
		{
			sourceStatesCache.add(null);
			flowingStatesCache.add(null);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(LEVEL);
		builder.add(IS_SOURCE);
	}

	@Override
	public FluidState getFluidState(BlockState blockState)
	{
		int level = blockState.getValue(LEVEL);
		boolean isSource = blockState.getValue(IS_SOURCE);

		if (!fluidStateCachesInitialized) initFluidStateCache();

		return isSource ? sourceStatesCache.get(level) : flowingStatesCache.get(level);
	}

	@Override
	protected synchronized void initFluidStateCache()
	{
		if (!fluidStateCachesInitialized) {
			for (int i = 1; i < 9; i++)
			{
				flowingStatesCache.set(i - 1, getFluid().getFlowing(9 - i, false));
				flowingStatesCache.set(i + 7, getFluid().getFlowing(9 - i, true));
				sourceStatesCache.set(i - 1, getFluid().getSource(false).setValue(FlowingFluid.LEVEL, 9 - i));
				sourceStatesCache.set(i + 7, getFluid().getSource(true).setValue(FlowingFluid.LEVEL, 9 - i));
			}

			fluidStateCachesInitialized = true;
		}
	}
}
