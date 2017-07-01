package squeek.veganoption.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockHay;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.helpers.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockRettable extends BlockHay implements IProbeInfoAccessor
{
	// this cannot be any higher than 3 due to BlockRotatablePillar using the 3rd/4th bits
	public static final int NUM_RETTING_STAGES = 3;
	public static final PropertyInteger STAGE = PropertyInteger.create("retting_stage", 0, NUM_RETTING_STAGES);

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

	@Nonnull
	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, STAGE, AXIS);
	}

	@Nonnull
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
		return stage >= NUM_RETTING_STAGES;
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
	public boolean canSilkHarvest(World world, BlockPos pos, @Nonnull IBlockState state, EntityPlayer player)
	{
		return false;
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		if (isRetted(state))
			return rettedItem;
		else
			return super.getItemDropped(state, random, fortune);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, @Nonnull Random random)
	{
		if (isRetted(state))
			return RandomHelper.getRandomIntFromRange(random, minRettedItemDrops, maxRettedItemDrops);
		else
			return super.quantityDropped(state, fortune, random);
	}

	@Override
	public boolean isToolEffective(String type, @Nonnull IBlockState state)
	{
		if (isRetted(state))
			return false;
		else
			return super.isToolEffective(type, state);
	}

	@Override
	public void setHarvestLevel(@Nonnull String toolClass, int level, @Nonnull IBlockState state)
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
		rettingStage = Math.max(0, Math.min(NUM_RETTING_STAGES, rettingStage));
		world.setBlockState(pos, world.getBlockState(pos).withProperty(STAGE, rettingStage));

		if (isRetted(rettingStage))
		{
			finishRetting(world, pos);
		}
	}

	public static float getRettingPercent(IBlockState state)
	{
		return (float) state.getValue(STAGE) / NUM_RETTING_STAGES;
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
		return MathHelper.floor(getRettingPercent(world, pos) * MiscHelper.MAX_REDSTONE_SIGNAL_STRENGTH);
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
	{
		float rettingPercent = getRettingPercent(blockState);
		if (rettingPercent >= 1)
			probeInfo.text(LangHelper.translate("info.retted"));
		else
		{
			if (canRet(world, data.getPos()))
				probeInfo.text(LangHelper.contextString("top", "retting", Math.round(rettingPercent * 100F)));
			else
				probeInfo.text(LangHelper.translate("info.retting.not.submerged"));
		}
	}

	public static class ColorHandler implements IBlockColor, IItemColor
	{
		public final int baseColor;
		public final int rettedColor;

		public ColorHandler(int baseColor, int rettedColor)
		{
			this.baseColor = baseColor;
			this.rettedColor = rettedColor;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public int colorMultiplier(@Nonnull IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex)
		{
			if (world == null || pos == null)
				return baseColor;
			else if (isRetted(world, pos))
				return rettedColor;
			else
				return ColorHelper.blendBetweenColors(getRettingPercent(world, pos), baseColor, rettedColor, 0D, 1D);
		}

		@Override
		public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex)
		{
			// TODO: Handle retting stage based on ItemStack metadata
			return baseColor;
		}
	}
}
