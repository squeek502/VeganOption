package squeek.veganoption.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.LangHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockBasin extends Block implements IHollowBlock, IProbeInfoAccessor
{
	public static final PropertyBool IS_OPEN = PropertyBool.create("is_open");
	public static final double SIDE_WIDTH = 0.125D;

	public BlockBasin(Material material)
	{
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, IS_OPEN);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = BlockHelper.getTileEntitySafely(world, pos);
		boolean open = false;
		if (tile != null && tile instanceof TileEntityBasin)
		{
			open = ((TileEntityBasin) tile).isOpen();
		}
		return state.withProperty(IS_OPEN, open);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
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
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
	{
		super.neighborChanged(state, world, pos, block);

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityBasin)
		{
			((TileEntityBasin) tile).setPowered(world.isBlockPowered(pos));
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
			((TileEntityBasin) tile).setPowered(world.isBlockPowered(pos));
			((TileEntityBasin) tile).scheduleFluidConsume();
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ))
			return true;

		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof TileEntityBasin && ((TileEntityBasin) tile).onBlockActivated(player, hand, side, hitX, hitY, hitZ);
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
		double minX = FULL_BLOCK_AABB.minX, minY = FULL_BLOCK_AABB.minY, minZ = FULL_BLOCK_AABB.minZ;
		double maxX = FULL_BLOCK_AABB.maxX, maxY = FULL_BLOCK_AABB.maxY, maxZ = FULL_BLOCK_AABB.maxZ;

		if (side.getFrontOffsetX() != 0)
		{
			if (side.getFrontOffsetX() > 0)
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
		if (side.getFrontOffsetY() != 0)
		{
			if (side.getFrontOffsetY() > 0)
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
		if (side.getFrontOffsetZ() != 0)
		{
			if (side.getFrontOffsetZ() > 0)
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

		return new AxisAlignedBB(offsetX + minX, offsetY + minY, offsetZ + minZ, offsetX + maxX, offsetY + maxY, offsetZ + maxZ);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB collidingAABB, @Nonnull List<AxisAlignedBB> collidingBoundingBoxes, Entity collidingEntity)
	{
		// hack...
		// this function is called with a null entity in World.isBlockFullCube
		if (collidingEntity == null)
			return;

		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityBasin && ((TileEntityBasin) tile).isPowered())
		{
			for (EnumFacing side : EnumFacing.VALUES)
			{
				if (side == EnumFacing.UP)
					continue;

				List<AxisAlignedBB> AABBs = new ArrayList<AxisAlignedBB>(4);

				AABBs.add(getSideBoundingBox(side, pos.getX(), pos.getY(), pos.getZ()));

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
			AxisAlignedBB AABB = getOuterBoundingBox(world, pos.getX(), pos.getY(), pos.getZ());

			if (AABB != null && collidingAABB.intersectsWith(AABB))
			{
				collidingBoundingBoxes.add(AABB);
			}
		}
	}

	public AxisAlignedBB getOuterBoundingBox(World world, int x, int y, int z)
	{
		return new AxisAlignedBB(x + FULL_BLOCK_AABB.minX, y + FULL_BLOCK_AABB.minY, z + FULL_BLOCK_AABB.minZ,
								 x + FULL_BLOCK_AABB.maxX, y + FULL_BLOCK_AABB.maxY, z + FULL_BLOCK_AABB.maxZ);
	}

	public AxisAlignedBB getInnerBoundingBox(World world, int x, int y, int z)
	{
		return new AxisAlignedBB(x + FULL_BLOCK_AABB.minX + SIDE_WIDTH, y + FULL_BLOCK_AABB.minY + SIDE_WIDTH, z + FULL_BLOCK_AABB.minZ + SIDE_WIDTH,
								 x + FULL_BLOCK_AABB.maxX - SIDE_WIDTH, y + FULL_BLOCK_AABB.maxY - SIDE_WIDTH, z + FULL_BLOCK_AABB.maxZ - SIDE_WIDTH);
	}

	/*
	 * IHollowBlock
	 */
	@Override
	public boolean isBlockFullCube(World world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
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
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
	{
		TileEntityBasin basin = (TileEntityBasin) world.getTileEntity(data.getPos());
		if (basin == null)
			return;
		probeInfo.text(LangHelper.translate("info.basin." + (basin.isPowered() ? "open" : "closed")));

	}
}
