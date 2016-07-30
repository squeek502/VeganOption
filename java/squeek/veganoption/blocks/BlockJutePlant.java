package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.content.modules.Jute;

public class BlockJutePlant extends BlockBush implements IGrowable
{
	IIcon[] blockIcons = new IIcon[NUM_ICONS];
	public static final int NUM_BOTTOM_STAGES = 6;
	public static final int NUM_TOP_STAGES = 5;
	public static final int NUM_GROWTH_STAGES = NUM_BOTTOM_STAGES + NUM_TOP_STAGES;
	public static final int BOTTOM_META_FULL = NUM_BOTTOM_STAGES;
	public static final int BOTTOM_META_GROWTH_MAX = BOTTOM_META_FULL - 1;
	public static final int TOP_META_START = BOTTOM_META_FULL + 1;
	public static final int META_MAX = TOP_META_START + NUM_TOP_STAGES;
	public static final int NUM_ICONS = 7;
	public static float GROWTH_CHANCE_PER_UPDATETICK = 0.10f;

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

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		return !isTop(meta) ? 1 : 0;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return Jute.juteSeeds;
	}

	public void deltaGrowth(World world, int x, int y, int z, int delta)
	{
		if (world.getBlock(x, y, z) != this)
			return;

		int oldMeta = world.getBlockMetadata(x, y, z);

		if (oldMeta == BOTTOM_META_FULL && world.getBlock(x, y + 1, z) == this)
		{
			deltaGrowth(world, x, y + 1, z, delta);
			return;
		}

		int newMeta = oldMeta + delta;

		if (isFullyGrown(newMeta))
		{
			Blocks.double_plant.func_149889_c(world, x, y - 1, z, Jute.FERN_METADATA, 3);
		}
		else
		{
			if (newMeta <= BOTTOM_META_GROWTH_MAX || (newMeta >= TOP_META_START && oldMeta >= TOP_META_START))
			{
				world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
			}
			else if (oldMeta <= BOTTOM_META_GROWTH_MAX && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z))
			{
				int metaTransferred = newMeta - BOTTOM_META_FULL;
				world.setBlockMetadataWithNotify(x, y, z, BOTTOM_META_FULL, 3);
				world.setBlock(x, y + 1, z, this, TOP_META_START + metaTransferred, 3);
			}
			else if (oldMeta <= BOTTOM_META_GROWTH_MAX)
			{
				newMeta = Math.min(newMeta, BOTTOM_META_GROWTH_MAX);
				world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
			}
		}
	}

	public float getGrowthPercent(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == BOTTOM_META_FULL && world.getBlock(x, y + 1, z) == this)
			return getGrowthPercent(world, x, y + 1, z);

		int growthMeta = isTop(meta) ? meta - 1 : meta;

		return (float) growthMeta / NUM_GROWTH_STAGES;
	}

	public static boolean isFullyGrown(int meta)
	{
		return meta >= META_MAX;
	}

	public static boolean isTop(int meta)
	{
		return meta >= TOP_META_START;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		super.updateTick(world, x, y, z, random);

		boolean shouldGrow = random.nextFloat() < GROWTH_CHANCE_PER_UPDATETICK;
		if (shouldGrow && world.getBlockMetadata(x, y, z) != BOTTOM_META_FULL)
			deltaGrowth(world, x, y, z, 1);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		if (world.getBlock(x, y, z) != this)
			return super.canBlockStay(world, x, y, z);

		int meta = world.getBlockMetadata(x, y, z);
		if (meta == BOTTOM_META_FULL)
			return world.getBlock(x, y + 1, z) == this;
		if (isTop(meta))
			return world.getBlock(x, y - 1, z) == this;

		return super.canBlockStay(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z)
	{
		return world.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		int iconIndex = isTop(meta) ? meta - TOP_META_START : meta;
		if (iconIndex < blockIcons.length)
			return blockIcons[iconIndex];
		else
			return super.getIcon(side, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		for (int i = 0; i < NUM_ICONS; i++)
		{
			blockIcons[i] = register.registerIcon(getTextureName() + "_" + i);
		}
	}

	/**
	 * isNotFullyGrown
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote)
	{
		return true;
	}

	/**
	 * canBonemeal
	 */
	@Override
	public boolean func_149852_a(World world, Random random, int x, int y, int z)
	{
		return true;
	}

	/**
	 * onBonemeal
	 */
	@Override
	public void func_149853_b(World world, Random random, int x, int y, int z)
	{
		int deltaGrowth = MathHelper.getRandomIntegerInRange(random, 2, 5);
		deltaGrowth(world, x, y, z, deltaGrowth);
	}
}
