package squeek.veganoption.blocks;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.PlantType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockJutePlant extends BushBlock implements BonemealableBlock
{
	public static final int NUM_BOTTOM_STAGES = 6;
	public static final int NUM_TOP_STAGES = 5;
	public static final int NUM_GROWTH_STAGES = NUM_BOTTOM_STAGES + NUM_TOP_STAGES;
	public static final int GROWTH_STAGE_BOTTOM_WITH_TOP = NUM_GROWTH_STAGES;
	public static final float GROWTH_CHANCE_PER_UPDATETICK = 0.10f;

	public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth", 0, NUM_GROWTH_STAGES);

	public BlockJutePlant()
	{
		super(BlockBehaviour.Properties.of()
			.mapColor(MapColor.GRASS)
			.replaceable()
			.noCollission()
			.instabreak()
			.sound(SoundType.GRASS)
			.offsetType(BlockBehaviour.OffsetType.XYZ)
			.ignitedByLava()
			.pushReaction(PushReaction.DESTROY));
		registerDefaultState(getStateDefinition().any().setValue(GROWTH_STAGE, 0));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
	{
		float growthPercent = getGrowthPercent(getter, pos, state);
		return Block.box(2, 0, 2, 14, 14 * growthPercent, 14);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(GROWTH_STAGE);
	}

	public void deltaGrowth(Level level, BlockPos pos, BlockState state, int delta)
	{
		if (state.getBlock() != this)
			return;

		int oldGrowthStage = state.getValue(GROWTH_STAGE);

		if (hasTop(state))
		{
			deltaGrowth(level, pos.above(), level.getBlockState(pos.above()), delta);
			return;
		}

		int newGrowthStage = oldGrowthStage + delta;

		if (isFullyGrown(newGrowthStage))
		{
			// set to air preemptively to avoid canBlockStay shenanigans
			level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 0);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
			level.setBlockAndUpdate(pos.below(), Blocks.LARGE_FERN.defaultBlockState());
		}
		else
		{
			boolean isGrowingToTop = !isTop(oldGrowthStage) && isTop(newGrowthStage);
			if (!isGrowingToTop)
			{
				level.setBlockAndUpdate(pos, state.setValue(GROWTH_STAGE, newGrowthStage));
			}
			else if (level.getBlockState(pos.above()).isAir())
			{
				level.setBlockAndUpdate(pos, state.setValue(GROWTH_STAGE, GROWTH_STAGE_BOTTOM_WITH_TOP));
				level.setBlockAndUpdate(pos.above(), defaultBlockState().setValue(GROWTH_STAGE, newGrowthStage));
			}
			else
			{
				newGrowthStage = Math.min(newGrowthStage, NUM_BOTTOM_STAGES);
				if (newGrowthStage != oldGrowthStage)
					level.setBlockAndUpdate(pos, state.setValue(GROWTH_STAGE, newGrowthStage));
			}
		}
	}

	public float getGrowthPercent(BlockGetter getter, BlockPos pos, BlockState state)
	{
		if (hasTop(state))
			return getGrowthPercent(getter, pos.above(), getter.getBlockState(pos.above()));

		return (float) state.getValue(GROWTH_STAGE) / NUM_GROWTH_STAGES;
	}

	public static boolean isFullyGrown(int growthStage)
	{
		return growthStage >= NUM_GROWTH_STAGES;
	}

	public static boolean isFullyGrown(BlockState state)
	{
		return isFullyGrown(state.getValue(GROWTH_STAGE));
	}

	public static boolean isTop(int growthStage)
	{
		return growthStage >= NUM_BOTTOM_STAGES && growthStage != GROWTH_STAGE_BOTTOM_WITH_TOP;
	}

	public static boolean isTop(BlockState state)
	{
		return isTop(state.getValue(GROWTH_STAGE));
	}

	public static boolean hasTop(int growthStage)
	{
		return growthStage == GROWTH_STAGE_BOTTOM_WITH_TOP;
	}

	public static boolean hasTop(@Nonnull BlockState state)
	{
		return hasTop(state.getValue(GROWTH_STAGE));
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		super.randomTick(state, level, pos, random);

		boolean shouldGrow = random.nextFloat() < GROWTH_CHANCE_PER_UPDATETICK;
		if (shouldGrow && !hasTop(state))
			deltaGrowth(level, pos, state, 1);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
	{
		if (state.getBlock() != this)
			return super.canSurvive(state, reader, pos);
		if (hasTop(state))
			return reader.getBlockState(pos.above()).getBlock() == this;
		if (isTop(state))
			return reader.getBlockState(pos.above()).getBlock() == this;
		return super.canSurvive(state, reader, pos);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader reader, BlockPos pos, BlockState state)
	{
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state)
	{
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state)
	{
		int deltaGrowth = Mth.randomBetweenInclusive(random, 2, 5);
		deltaGrowth(level, pos, state, deltaGrowth);
	}

	@Override
	public PlantType getPlantType(BlockGetter level, BlockPos pos)
	{
		return PlantType.PLAINS;
	}

	public static class ColorHandler implements BlockColor, ItemColor
	{
		@Override
		public int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex)
		{
			if (getter == null || pos == null)
				return GrassColor.get(0.5d, 1d);

			return BiomeColors.getAverageGrassColor(getter, pos);
		}

		@Override
		public int getColor(ItemStack stack, int tintIndex)
		{
			return GrassColor.get(0.5d, 1d);
		}
	}
}
