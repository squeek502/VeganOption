package squeek.veganoption.blocks;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.MiscHelper;

import javax.annotation.Nullable;

public class BlockRettable extends HayBlock
{
	public static final int MAX_RETTING_STAGES = 3;
	public static final IntegerProperty STAGE = IntegerProperty.create("retting_stage", 0, MAX_RETTING_STAGES);

	private RegistryObject<Item> rettedItem;
	private int minRettedItemDrops;
	private int maxRettedItemDrops;

	public BlockRettable(RegistryObject<Item> rettedItem, int minRettedItemDrops, int maxRettedItemDrops)
	{
		super(BlockBehaviour.Properties.of()
			.sound(SoundType.GRASS)
			.strength(0.5f)
			.randomTicks()
			.mapColor(MapColor.COLOR_GREEN));
		this.rettedItem = rettedItem;
		this.minRettedItemDrops = minRettedItemDrops;
		this.maxRettedItemDrops = maxRettedItemDrops;
		registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(STAGE);
		super.createBlockStateDefinition(builder);
	}

	public Item getRettedItem()
	{
		return rettedItem.get();
	}

	public int getMinRettedItemDrops()
	{
		return minRettedItemDrops;
	}

	public int getMaxRettedItemDrops()
	{
		return maxRettedItemDrops;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		super.randomTick(state, level, pos, random);
		if (canRet(level, pos) && !isRetted(level, pos))
			deltaRettingStage(level, pos, 1);
	}

	public void finishRetting(Level level, BlockPos pos)
	{
	}

	public boolean canRet(Level level, BlockPos pos)
	{
		return BlockHelper.isAdjacentToOrCoveredInWater(level, pos);
	}

	public static boolean isRetted(int stage)
	{
		return stage >= MAX_RETTING_STAGES;
	}

	public static boolean isRetted(BlockState state)
	{
		return isRetted(state.getValue(STAGE));
	}

	public static boolean isRetted(BlockGetter getter, BlockPos pos)
	{
		return isRetted(getter.getBlockState(pos));
	}

	public void deltaRettingStage(Level level, BlockPos pos, int deltaRetting)
	{
		setRettingStage(level, pos, level.getBlockState(pos).getValue(STAGE) + deltaRetting);
	}

	public void setRettingStage(Level level, BlockPos pos, int rettingStage)
	{
		rettingStage = Math.max(0, Math.min(MAX_RETTING_STAGES, rettingStage));
		level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(STAGE, rettingStage));

		if (isRetted(rettingStage))
			finishRetting(level, pos);
	}

	public static float getRettingPercent(BlockState state)
	{
		return (float) state.getValue(STAGE) / MAX_RETTING_STAGES;
	}

	public static float getRettingPercent(BlockGetter getter, BlockPos pos)
	{
		return getRettingPercent(getter.getBlockState(pos));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		return Mth.floor(getRettingPercent(state) * MiscHelper.MAX_REDSTONE_SIGNAL_STRENGTH);
	}

	public static class ColorHandler implements BlockColor, ItemColor
	{
		public final int baseColor;
		public final int rettedColor;

		public ColorHandler(int baseColor, int rettedColor)
		{
			this.baseColor = baseColor;
			this.rettedColor = rettedColor;
		}

		@Override
		public int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex)
		{
			if (getter == null || pos == null)
				return baseColor;
			else if (isRetted(state))
				return rettedColor;
			else
				return ColorHelper.blendBetweenColors(getRettingPercent(state), baseColor, rettedColor, 0D, 1D);
		}

		@Override
		public int getColor(ItemStack stack, int tintIndex)
		{
			// TODO
			return baseColor;
		}
	}
}
