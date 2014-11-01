package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.BlockHay;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.RandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRettable extends BlockHay
{
	// this cannot be any higher than 3 due to BlockRotatablePillar using the 3rd/4th bits
	public int numRettingStages = 3;
	public int baseColor = 0x67ce0c;
	public int rettedColor = 0xbfb57e;
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
	}

	public BlockRettable(Item rettedItem, int minRettedItemDrops, int maxRettedItemDrops, int baseColor, int rettedColor)
	{
		this(rettedItem, minRettedItemDrops, maxRettedItemDrops);
		this.baseColor = baseColor;
		this.rettedColor = rettedColor;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z)
	{
		if (isRetted(world, x, y, z))
			return rettedColor;
		else
			return ColorHelper.blendBetweenColors(getRettingPercent(world, x, y, z), baseColor, rettedColor, 0d, 1d);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBlockColor()
	{
		return baseColor;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderColor(int p_149741_1_)
	{
		return baseColor;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		super.updateTick(world, x, y, z, random);

		if (canRet(world, x, y, z) && !isRetted(world, x, y, z))
		{
			deltaRettingStage(world, x, y, z, 1);
		}
	}

	public void finishRetting(World world, int x, int y, int z)
	{
	}

	public boolean canRet(World world, int x, int y, int z)
	{
		return BlockHelper.isAdjacentToOrCoveredInWater(BlockHelper.blockPos(world, x, y, z));
	}

	public boolean isRetted(int meta)
	{
		return getRettingStageFromMeta(meta) >= numRettingStages;
	}

	public boolean isRetted(IBlockAccess world, int x, int y, int z)
	{
		return isRetted(world.getBlockMetadata(x, y, z));
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		if (isRetted(meta))
			return rettedItem;
		else
			return super.getItemDropped(meta, random, fortune);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		if (isRetted(meta))
			return RandomHelper.getRandomIntFromRange(random, minRettedItemDrops, maxRettedItemDrops);
		else
			return super.quantityDropped(meta, fortune, random);
	}

	@Override
	public boolean isToolEffective(String type, int metadata)
	{
		if (isRetted(metadata))
			return false;
		else
			return super.isToolEffective(type, metadata);
	}

	@Override
	public int damageDropped(int p_149692_1_)
	{
		return 0;
	}

	public void deltaRettingStage(World world, int x, int y, int z, int deltaRetting)
	{
		setRettingStage(world, x, y, z, getRettingStage(world, x, y, z) + deltaRetting);
	}

	public void setRettingStage(World world, int x, int y, int z, int rettingStage)
	{
		rettingStage = Math.max(0, Math.min(numRettingStages, rettingStage));
		int metadata = getMetaFromRettingStage(world, x, y, z, rettingStage);
		world.setBlockMetadataWithNotify(x, y, z, metadata, 3);

		if (isRetted(metadata))
		{
			finishRetting(world, x, y, z);
		}
	}

	public float getRettingPercent(IBlockAccess world, int x, int y, int z)
	{
		return getRettingPercentFromMeta(world.getBlockMetadata(x, y, z));
	}

	public float getRettingPercentFromMeta(int meta)
	{
		return (float) getRettingStageFromMeta(meta) / numRettingStages;
	}

	public int getRettingStage(IBlockAccess world, int x, int y, int z)
	{
		return getRettingStageFromMeta(world.getBlockMetadata(x, y, z));
	}

	public int getRettingStageFromMeta(int meta)
	{
		return meta & 3;
	}

	public int getMetaFromRettingStage(IBlockAccess world, int x, int y, int z, int rettingStage)
	{
		return (world.getBlockMetadata(x, y, z) & 12) + rettingStage;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		return MathHelper.floor_float(getRettingPercent(world, x, y, z) * MiscHelper.MAX_REDSTONE_SIGNAL_STRENGTH);
	}
}
