package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.BlockHay;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.RandomHelper;

import javax.annotation.Nullable;

public class BlockRettable extends BlockHay
{
	// this cannot be any higher than 3 due to BlockRotatablePillar using the 3rd/4th bits
	public static final int numRettingStages = 3;
	public static final PropertyInteger STAGE = PropertyInteger.create("retting_stage", 0, numRettingStages - 1);

	public Item rettedItem;
	public int minRettedItemDrops;
	public int maxRettedItemDrops;

	public BlockRettable(Item rettedItem, int minRettedItemDrops, int maxRettedItemDrops)
	{
		super();
		this.setTickRandomly(true);
		this.rettedItem = rettedItem;
		this.minRettedItemDrops = minRettedItemDrops;
		this.maxRettedItemDrops = maxRettedItemDrops;
		setSoundType(SoundType.GROUND);
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, STAGE, AXIS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState axisState = super.getStateFromMeta(meta);
		EnumFacing.Axis axis = axisState.getValue(AXIS);
		int stage = meta & 3;
		return getDefaultState().withProperty(STAGE, stage).withProperty(AXIS, axis);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int stage = state.getValue(STAGE);
		return (super.getMetaFromState(state) & 12) + stage;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		super.updateTick(world, pos, state, random);

		if (canRet(world, pos) && !isRetted(world, pos))
		{
			deltaRettingStage(world, pos, 1);
		}
	}

	public void finishRetting(World world, BlockPos pos)
	{
	}

	public boolean canRet(World world, BlockPos pos)
	{
		return BlockHelper.isAdjacentToOrCoveredInWater(world, pos);
	}

	public static boolean isRetted(int stage)
	{
		return stage >= numRettingStages;
	}

	public static boolean isRetted(IBlockState state)
	{
		return isRetted(state.getValue(STAGE));
	}

	public static boolean isRetted(IBlockAccess world, BlockPos pos)
	{
		return isRetted(world.getBlockState(pos));
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		return false;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		if (isRetted(state))
			return rettedItem;
		else
			return super.getItemDropped(state, random, fortune);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random)
	{
		if (isRetted(state))
			return RandomHelper.getRandomIntFromRange(random, minRettedItemDrops, maxRettedItemDrops);
		else
			return super.quantityDropped(state, fortune, random);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state)
	{
		if (isRetted(state))
			return false;
		else
			return super.isToolEffective(type, state);
	}

	@Override
	public void setHarvestLevel(String toolClass, int level, IBlockState state)
	{
		if (isRetted(state))
			return;

		super.setHarvestLevel(toolClass, level, state);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	public void deltaRettingStage(World world, BlockPos pos, int deltaRetting)
	{
		setRettingStage(world, pos, world.getBlockState(pos).getValue(STAGE) + deltaRetting);
	}

	public void setRettingStage(World world, BlockPos pos, int rettingStage)
	{
		rettingStage = Math.max(0, Math.min(numRettingStages, rettingStage));
		world.setBlockState(pos, world.getBlockState(pos).withProperty(STAGE, rettingStage));

		if (isRetted(rettingStage))
		{
			finishRetting(world, pos);
		}
	}

	public static float getRettingPercent(IBlockState state)
	{
		return state.getValue(STAGE) / numRettingStages;
	}

	public static float getRettingPercent(IBlockAccess world, BlockPos pos)
	{
		return getRettingPercent(world.getBlockState(pos));
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos)
	{
		return MathHelper.floor_float(getRettingPercent(world, pos) * MiscHelper.MAX_REDSTONE_SIGNAL_STRENGTH);
	}

	public static class BlockRettableColorHandler implements IBlockColor {
		public static final int baseColor = 0x67ce0c;
		public static final int rettedColor = 0xbfb57e;

		@SideOnly(Side.CLIENT)
		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex)
		{
			if (isRetted(world, pos))
				return rettedColor;
			else
				return ColorHelper.blendBetweenColors(getRettingPercent(world, pos), baseColor, rettedColor, 0d, 1d);
		}
	}
}
