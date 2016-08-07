package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.content.modules.Jute;

import javax.annotation.Nullable;

public class BlockJutePlant extends BlockBush implements IGrowable
{
	public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = BlockDoublePlant.HALF;
	public static final PropertyBool HAS_TOP = PropertyBool.create("has_top");
	public static final PropertyInteger GROWTH_STAGE = PropertyInteger.create("growth", 0, BlockJutePlant.NUM_GROWTH_STAGES);

	public static final int NUM_BOTTOM_STAGES = 6;
	public static final int NUM_TOP_STAGES = 5;
	public static final int NUM_GROWTH_STAGES = NUM_BOTTOM_STAGES + NUM_TOP_STAGES;
	public static final int BOTTOM_META_FULL = NUM_BOTTOM_STAGES;
	public static final int BOTTOM_META_GROWTH_MAX = BOTTOM_META_FULL - 1;
	public static final int TOP_META_START = BOTTOM_META_FULL + 1;
	public static final int META_MAX = TOP_META_START + NUM_TOP_STAGES;
	public static final int META_INVALID = 15;
	public static final int NUM_ICONS = 7;
	public static final float GROWTH_CHANCE_PER_UPDATETICK = 0.10f;

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, HALF, HAS_TOP, GROWTH_STAGE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		float growthPercent = getGrowthPercent(source, pos, state);
		return new AxisAlignedBB(0.15F, 0.0F, 0.15F, 0.85F, 0.25f + growthPercent * 0.75f, 0.85F);
	}

	// TODO: Confirm that the above works the same as below
	/*
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		int normalizedMeta = isTop(meta) ? meta - TOP_META_START : meta;
		if (normalizedMeta >= 4)
			normalizedMeta++;
		float growthPercent = isTop(meta) ? (float) normalizedMeta / NUM_TOP_STAGES : (float) normalizedMeta / NUM_BOTTOM_STAGES;
		growthPercent = Math.min(1.0f, growthPercent);

		setBlockBounds(0.15F, 0.0F, 0.15F, 0.85F, 0.25f + growthPercent * 0.75f, 0.85F);
	}
	*/

	@Override
	public int getMetaFromState(IBlockState state)
	{
		// invalid state combinations all return the same meta
		if ((isTop(state) && hasTop(state))
			|| (isTop(state) && state.getValue(GROWTH_STAGE) < NUM_BOTTOM_STAGES)
			|| (!isTop(state) && state.getValue(GROWTH_STAGE) >= NUM_BOTTOM_STAGES)
			|| (hasTop(state) && state.getValue(GROWTH_STAGE) != NUM_BOTTOM_STAGES - 1))
			return META_INVALID;

		if (isTop(state))
			return TOP_META_START + state.getValue(GROWTH_STAGE) - NUM_BOTTOM_STAGES;

		if (hasTop(state))
			return BOTTOM_META_FULL;

		return state.getValue(GROWTH_STAGE);
	}

	public IBlockState getInvalidState()
	{
		return getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER).withProperty(HAS_TOP, true);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if (meta > META_MAX)
		{
			return getInvalidState();
		}

		boolean isTop = meta >= TOP_META_START;
		boolean hasTop = meta == BOTTOM_META_FULL;
		int growthStage = isTop ? meta - 1 : (hasTop ? NUM_BOTTOM_STAGES - 1 : meta);

		return getDefaultState()
			.withProperty(HALF, isTop ? BlockDoublePlant.EnumBlockHalf.UPPER : BlockDoublePlant.EnumBlockHalf.LOWER)
			.withProperty(GROWTH_STAGE, growthStage)
			.withProperty(HAS_TOP, hasTop);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random)
	{
		return !isTop(state) ? 1 : 0;
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Jute.juteSeeds;
	}

	public void deltaGrowth(World world, BlockPos pos, IBlockState state, int delta)
	{
		if (state.getBlock() != this)
			return;

		int oldGrowthStage = state.getValue(GROWTH_STAGE);

		if (hasTop(state) && world.getBlockState(pos.up()).getBlock() == this)
		{
			deltaGrowth(world, pos.up(), world.getBlockState(pos.up()), delta);
			return;
		}

		int newGrowthStage = oldGrowthStage + delta;

		if (isFullyGrown(newGrowthStage))
		{
			Blocks.DOUBLE_PLANT.placeAt(world, pos.down(), BlockDoublePlant.EnumPlantType.FERN, 3);
		}
		else
		{
			boolean isGrowingToTop = !isTop(state) && newGrowthStage >= NUM_BOTTOM_STAGES;
			if (!isGrowingToTop)
			{
				world.setBlockState(pos, state.withProperty(GROWTH_STAGE, newGrowthStage), 3);
			}
			else if (world.getBlockState(pos.up()).getBlock().isAir(world.getBlockState(pos.up()), world, pos.up()))
			{
				world.setBlockState(pos, state.withProperty(HAS_TOP, true).withProperty(GROWTH_STAGE, NUM_BOTTOM_STAGES - 1));
				IBlockState topState = getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER).withProperty(GROWTH_STAGE, newGrowthStage).withProperty(HAS_TOP, false);
				world.setBlockState(pos.up(), topState, 3);
			}
			else
			{
				newGrowthStage = Math.min(newGrowthStage, NUM_BOTTOM_STAGES - 1);
				if (newGrowthStage != oldGrowthStage)
					world.setBlockState(pos, state.withProperty(GROWTH_STAGE, newGrowthStage));
			}
		}
	}

	public float getGrowthPercent(IBlockAccess world, BlockPos pos, IBlockState state)
	{
		if (hasTop(state) && world.getBlockState(pos.up()).getBlock() == this)
			return getGrowthPercent(world, pos.up(), world.getBlockState(pos.up()));

		return (float) state.getValue(GROWTH_STAGE) / NUM_GROWTH_STAGES;
	}

	public static boolean isFullyGrown(int growthStage)
	{
		return growthStage >= NUM_GROWTH_STAGES;
	}

	public static boolean isFullyGrown(IBlockState state)
	{
		return isFullyGrown(state.getValue(GROWTH_STAGE));
	}

	public static boolean isTop(IBlockState state)
	{
		return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
	}

	public static boolean hasTop(IBlockState state)
	{
		return state.getValue(HAS_TOP);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		super.updateTick(world, pos, state, random);

		boolean shouldGrow = random.nextFloat() < GROWTH_CHANCE_PER_UPDATETICK;
		if (shouldGrow && !hasTop(state))
			deltaGrowth(world, pos, state, 1);
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
	{
		if (state.getBlock() != this)
			return super.canBlockStay(world, pos, state);

		if (hasTop(state))
			return world.getBlockState(pos.up()).getBlock() == this;
		if (isTop(state))
			return world.getBlockState(pos.down()).getBlock() == this;

		return super.canBlockStay(world, pos, state);
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
	{
		return true;
	}

	@Override
	public boolean canUseBonemeal(World world, Random random, BlockPos pos, IBlockState state)
	{
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, IBlockState state)
	{
		int deltaGrowth = MathHelper.getRandomIntegerInRange(random, 2, 5);
		deltaGrowth(world, pos, state, deltaGrowth);
	}

	public static class BlockJutePlantColorHandler implements IBlockColor
	{
		@SideOnly(Side.CLIENT)
		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex)
		{
			return BiomeColorHelper.getGrassColorAtPos(world, pos);
		}
	}
}
