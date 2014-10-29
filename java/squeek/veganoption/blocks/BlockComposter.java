package squeek.veganoption.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
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
		int rotationMeta = DirectionHelper.getDirectionFromYaw(placer).ordinal();
		world.setBlockMetadataWithNotify(x, y, z, rotationMeta, 3);

		super.onBlockPlacedBy(world, x, y, z, placer, itemStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon("planks_oak");
	}
}
