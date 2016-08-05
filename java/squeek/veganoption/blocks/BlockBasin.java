package squeek.veganoption.blocks;

import java.util.List;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.blocks.tiles.TileEntityBasin;

public class BlockBasin extends BlockContainer implements IHollowBlock
{
	public static final double SIDE_WIDTH = 0.125D;

	public BlockBasin(Material material)
	{
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityBasin();
	}

	/*
	 * Misc properties
	 */
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		if (side != EnumFacing.UP)
			return true;

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityBasin)
		{
			return ((TileEntityBasin) tile).isClosed();
		}

		return true;
	}

	/*
	 * Events
	 */
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(world, pos, neighbor);

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityBasin)
		{
			((TileEntityBasin) tile).setPowered(tile.getWorld().isBlockIndirectlyGettingPowered(pos) > 0);
			((TileEntityBasin) tile).scheduleFluidConsume();
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(world, pos, state);

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityBasin)
		{
			((TileEntityBasin) tile).setPowered(world.isBlockIndirectlyGettingPowered(pos) > 0);
			((TileEntityBasin) tile).scheduleFluidConsume();
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ))
			return true;

		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof TileEntityBasin && ((TileEntityBasin) tile).onBlockActivated(player, side, hitX, hitY, hitZ);
	}

	/*
	 * Bounding box/collisions
	 */
	public AxisAlignedBB getSideBoundingBox(EnumFacing side)
	{
		return getSideBoundingBox(side, 0, 0, 0);
	}

	public AxisAlignedBB getSideBoundingBox(EnumFacing side, double offsetX, double offsetY, double offsetZ)
	{
		return getSideBoundingBox(side, offsetX, offsetY, offsetZ, 1f);
	}

	public AxisAlignedBB getSideBoundingBox(EnumFacing side, double offsetX, double offsetY, double offsetZ, float depthScale)
	{
		return getSideBoundingBox(side, offsetX, offsetY, offsetZ, depthScale, 1f, 1f);
	}

	public AxisAlignedBB getSideBoundingBox(EnumFacing side, double offsetX, double offsetY, double offsetZ, float depthScale, float widthScale, float heightScale)
	{
		double minX = this.minX, minY = this.minY, minZ = this.minZ;
		double maxX = this.maxX, maxY = this.maxY, maxZ = this.maxZ;

		if (side.offsetX != 0)
		{
			if (side.offsetX > 0)
				minX = maxX - SIDE_WIDTH * depthScale;
			else
				maxX = minX + SIDE_WIDTH * depthScale;
			if (widthScale != 1) // z axis
			{
				double width = maxZ - minZ;
				if (widthScale > 0)
					maxZ = minZ + width * widthScale;
				else
					minZ = maxZ + width * widthScale;
			}
			if (heightScale != 1) // y axis
			{
				double height = maxZ - minZ;
				if (heightScale > 0)
					maxY = minY + height * heightScale;
				else
					minY = maxY + height * heightScale;
			}
		}
		if (side.offsetY != 0)
		{
			if (side.offsetY > 0)
				minY = maxY - SIDE_WIDTH * depthScale;
			else
				maxY = minY + SIDE_WIDTH * depthScale;
			if (widthScale != 1) // z axis
			{
				double width = maxZ - minZ;
				if (widthScale > 0)
					maxZ = minZ + width * widthScale;
				else
					minZ = maxZ + width * widthScale;
			}
			if (heightScale != 1) // x axis
			{
				double height = maxX - minX;
				if (heightScale > 0)
					maxX = minX + height * heightScale;
				else
					minX = maxX + height * heightScale;
			}
		}
		if (side.offsetZ != 0)
		{
			if (side.offsetZ > 0)
				minZ = maxZ - SIDE_WIDTH * depthScale;
			else
				maxZ = minZ + SIDE_WIDTH * depthScale;
			if (widthScale != 1) // x axis
			{
				double width = maxX - minX;
				if (widthScale > 0)
					maxX = minX + width * widthScale;
				else
					minX = maxX + width * widthScale;
			}
			if (heightScale != 1) // y axis
			{
				double height = maxY - minY;
				if (heightScale > 0)
					maxY = minY + height * heightScale;
				else
					minY = maxY + height * heightScale;
			}
		}

		return AxisAlignedBB.getBoundingBox(offsetX + minX, offsetY + minY, offsetZ + minZ, offsetX + maxX, offsetY + maxY, offsetZ + maxZ);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collidingAABB, List collidingBoundingBoxes, Entity collidingEntity)
	{
		// hack...
		// this function is called with a null entity in World.isBlockFullCube
		if (collidingEntity == null)
			return;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityBasin && ((TileEntityBasin) tile).isPowered())
		{
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				if (side == ForgeDirection.UP)
					continue;

				List<AxisAlignedBB> AABBs = new ArrayList<AxisAlignedBB>(4);

				AABBs.add(getSideBoundingBox(side, x, y, z));

				for (AxisAlignedBB AABB : AABBs)
				{
					if (AABB != null && collidingAABB.intersectsWith(AABB))
					{
						collidingBoundingBoxes.add(AABB);
					}
				}
			}
		}
		else
		{
			AxisAlignedBB AABB = getOuterBoundingBox(world, x, y, z);

			if (AABB != null && collidingAABB.intersectsWith(AABB))
			{
				collidingBoundingBoxes.add(AABB);
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return getOuterBoundingBox(world, x, y, z);
	}

	public AxisAlignedBB getOuterBoundingBox(World world, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ,
											x + maxX, y + maxY, z + maxZ);
	}

	public AxisAlignedBB getInnerBoundingBox(World world, int x, int y, int z)
	{
		AxisAlignedBB AABB = AxisAlignedBB.getBoundingBox(x + minX + SIDE_WIDTH, y + minY + SIDE_WIDTH, z + minZ + SIDE_WIDTH,
															x + maxX - SIDE_WIDTH, y + maxY - SIDE_WIDTH, z + maxZ - SIDE_WIDTH);
		return AABB;
	}

	/*
	 * IHollowBlock
	 */
	@Override
	public boolean isBlockFullCube(World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if (tile instanceof TileEntityBasin)
		{
			return ((TileEntityBasin) tile).isClosed();
		}
		return true;
	}

	/*
	 * Rendering
	 */

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumBlockRenderType getRenderType()
	{
		return EnumBlockRenderType.MODEL;
	}


}
