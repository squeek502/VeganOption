package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.registry.Content;

public class BlockEnderRift extends BlockEndPortal
{
	public static class MaterialEnderRift extends MaterialLogic
	{
		public MaterialEnderRift()
		{
			super(MapColor.airColor);
			this.setImmovableMobility();
		}
	}

	public static MaterialEnderRift materialEnderRift = new MaterialEnderRift();

	public BlockEnderRift()
	{
		super(materialEnderRift);
		setTickRandomly(true);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityEnderRift();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 0.75F, 1.0F);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal();
	}

	// absord fluid flow
	public boolean onFluidFlowInto(World world, int x, int y, int z, int flowDecay)
	{
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		super.updateTick(world, x, y, z, random);

		BlockHelper.BlockPos riftBlockPos = BlockHelper.blockPos(world, x, y, z);
		BlockHelper.BlockPos aboveBlockPos = riftBlockPos.getOffset(0, 1, 0);
		if (BlockHelper.isWater(aboveBlockPos) && world.getBlock(x, y - 1, z).isReplaceable(world, x, y - 1, z))
		{
			BlockHelper.BlockPos sourceBlockToConsume = BlockHelper.followWaterStreamToSourceBlock(aboveBlockPos);
			if (sourceBlockToConsume != null)
			{
				world.setBlockToAir(sourceBlockToConsume.x, sourceBlockToConsume.y, sourceBlockToConsume.z);

				if (!world.isDaytime())
				{
					world.setBlock(x, y - 1, z, Content.rawEnder, 7, 3);
				}
				else
				{
					// TODO: negative consequences
					VeganOption.Log.info("too bad it's daytime");
				}
			}
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block changedBlock)
	{
		super.onNeighborBlockChange(world, x, y, z, changedBlock);

		if (!canBlockStay(world, x, y, z))
			world.setBlockToAir(x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return isValidPortalLocation(world, x, y, z) && super.canBlockStay(world, x, y, z);
	}

	public static boolean isValidPortalLocation(World world, int x, int y, int z)
	{
		BlockHelper.BlockPos blockPos = BlockHelper.blockPos(world, x, y, z);
		for (BlockHelper.BlockPos blockToCheck : BlockHelper.getBlocksAdjacentTo(blockPos))
		{
			if (!(blockToCheck.getBlock() instanceof BlockEncrustedObsidian))
				return false;
		}
		return true;
	}

	// stop from teleporting to the end
	@Override
	public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
	{
		return;
	}
}
