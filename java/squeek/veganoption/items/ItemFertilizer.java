package squeek.veganoption.items;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFertilizer extends Item
{
	public ItemFertilizer()
	{
		super();
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new ItemFertilizer.DispenserBehavior());
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (ItemDye.applyBonemeal(itemStack, world, x, y, z, player))
		{
			if (!world.isRemote)
			{
				world.playAuxSFX(2005, x, y, z, 0);
			}
			return true;
		}
		return false;
	}

	// copied from bonemeal's dispenser behavior in net.minecraft.init.Bootstrap
	public static class DispenserBehavior extends BehaviorDefaultDispenseItem
	{
		private boolean didFertilize = true;

		@Override
		protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
		{
			EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
			World world = blockSource.getWorld();
			int x = blockSource.getXInt() + enumfacing.getFrontOffsetX();
			int y = blockSource.getYInt() + enumfacing.getFrontOffsetY();
			int z = blockSource.getZInt() + enumfacing.getFrontOffsetZ();

			if (ItemDye.func_150919_a(itemStack, world, x, y, z))
			{
				if (!world.isRemote)
				{
					world.playAuxSFX(2005, x, y, z, 0);
				}
				this.didFertilize = true;
			}
			else
			{
				this.didFertilize = false;
			}

			return itemStack;
		}

		@Override
		protected void playDispenseSound(IBlockSource blockSource)
		{
			if (this.didFertilize)
			{
				blockSource.getWorld().playAuxSFX(1000, blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), 0);
			}
			else
			{
				blockSource.getWorld().playAuxSFX(1001, blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), 0);
			}
		}
	}
}
