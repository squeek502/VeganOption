package squeek.veganoption.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.helpers.DirectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockComposter extends BlockContainer
{
	public BlockComposter()
	{
		super(Material.wood);
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityComposter();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityComposter)
		{
			return ((TileEntityComposter) tile).onActivated(player, side, hitX, hitY, hitZ);
		}
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityComposter)
		{
			((TileEntityComposter) tile).onBlockBroken();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemStack)
	{
		int rotationMeta = DirectionHelper.getDirectionFromYaw(placer).getHorizontalIndex();
		world.setBlockMetadataWithNotify(x, y, z, rotationMeta, 3);

		super.onBlockPlacedBy(world, x, y, z, placer, itemStack);
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityComposter)
		{
			return ((TileEntityComposter) tile).getComparatorSignalStrength(side);
		}
		return super.getComparatorInputOverride(world, x, y, z, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon("planks_oak");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderInPass(int pass)
	{
		return pass <= getRenderBlockPass();
	}
}
