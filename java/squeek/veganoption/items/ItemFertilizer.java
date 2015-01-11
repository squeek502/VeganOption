package squeek.veganoption.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFertilizer extends Item
{
	public ItemFertilizer()
	{
		super();
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
}
