package squeek.veganoption.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.helpers.DirectionHelper;

public class BlockComposter extends BlockContainer
{
	public static final AxisAlignedBB COMPOSTER_AABB = new AxisAlignedBB(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public BlockComposter()
	{
		super(Material.WOOD);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityComposter();
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return COMPOSTER_AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityComposter)
		{
			return ((TileEntityComposter) tile).onActivated(player, side, hitX, hitY, hitZ);
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityComposter)
		{
			((TileEntityComposter) tile).onBlockBroken();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack)
	{
		EnumFacing rotation = DirectionHelper.getDirectionFromYaw(placer);
		world.setBlockState(pos, state.withProperty(FACING, rotation));

		super.onBlockPlacedBy(world, pos, state, placer, itemStack);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityComposter)
		{
			return ((TileEntityComposter) tile).getComparatorSignalStrength();
		}
		return super.getComparatorInputOverride(state, world, pos);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
}
