package squeek.veganoption.blocks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.veganoption.blocks.renderers.RenderBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasin extends BlockContainer implements IHollowBlock
{
	public static final double SIDE_WIDTH = 0.125D;

	public IIcon blockIconTopOpen;
	public IIcon blockIconTopClosed;
	public IIcon blockIconBottom;
	public IIcon blockIconSide;
	public static IIcon blockIconInner;

	public BlockBasin(Material material)
	{
		super(material);
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
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		if (side != ForgeDirection.UP)
			return true;

		TileEntity tile = world.getTileEntity(x, y, z);
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block changedBlock)
	{
		super.onNeighborBlockChange(world, x, y, z, changedBlock);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityBasin)
		{
			((TileEntityBasin) tile).setPowered(world.isBlockIndirectlyGettingPowered(x, y, z));
			((TileEntityBasin) tile).scheduleFluidConsume();
		}
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityBasin)
		{
			((TileEntityBasin) tile).setPowered(world.isBlockIndirectlyGettingPowered(x, y, z));
			((TileEntityBasin) tile).scheduleFluidConsume();
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ))
			return true;

		TileEntity tile = world.getTileEntity(x, y, z);
		return tile instanceof TileEntityBasin && ((TileEntityBasin) tile).onBlockActivated(player, side, hitX, hitY, hitZ);
	}

	/*
	 * Bounding box/collisions
	 */
	public AxisAlignedBB getSideBoundingBox(ForgeDirection side)
	{
		return getSideBoundingBox(side, 0, 0, 0);
	}

	public AxisAlignedBB getSideBoundingBox(ForgeDirection side, double offsetX, double offsetY, double offsetZ)
	{
		return getSideBoundingBox(side, offsetX, offsetY, offsetZ, 1f);
	}

	public AxisAlignedBB getSideBoundingBox(ForgeDirection side, double offsetX, double offsetY, double offsetZ, float depthScale)
	{
		return getSideBoundingBox(side, offsetX, offsetY, offsetZ, depthScale, 1f, 1f);
	}

	public AxisAlignedBB getSideBoundingBox(ForgeDirection side, double offsetX, double offsetY, double offsetZ, float depthScale, float widthScale, float heightScale)
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
		TileEntity tile = world.getTileEntity(x, y, z);
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
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return true;
	}

	@Override
	public int getRenderType()
	{
		return RenderBasin.renderId;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIconTopOpen = iconRegister.registerIcon(this.getTextureName() + "_top_open");
		blockIconTopClosed = iconRegister.registerIcon(this.getTextureName() + "_top_closed");
		blockIconBottom = iconRegister.registerIcon(this.getTextureName() + "_bottom");
		blockIconInner = iconRegister.registerIcon(this.getTextureName() + "_inner");
		blockIconSide = iconRegister.registerIcon(this.getTextureName() + "_side");
	}

	@Override
	public IIcon getIcon(int side, int metadata)
	{
		switch (ForgeDirection.getOrientation(side))
		{
			case DOWN:
				return blockIconBottom;
			case UP:
				return blockIconTopClosed;
			case EAST:
			case WEST:
			case NORTH:
			case SOUTH:
				return blockIconSide;
			case UNKNOWN:
				break;
		}
		return super.getIcon(side, metadata);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		if (ForgeDirection.getOrientation(side) == ForgeDirection.UP)
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityBasin && ((TileEntityBasin) tile).isOpen())
			{
				return blockIconTopOpen;
			}
		}

		return super.getIcon(world, x, y, z, side);
	}

	public static IIcon getInnerIcon()
	{
		return blockIconInner;
	}

}
