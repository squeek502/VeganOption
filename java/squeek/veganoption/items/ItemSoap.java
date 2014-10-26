package squeek.veganoption.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSoap extends Item
{
	@Override
	public EnumAction getItemUseAction(ItemStack itemStack)
	{
		return EnumAction.eat;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
	{
		return 32;
	}

	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			// I can not figure out how the curative item system
			// is meant to be used at all. PotionEffects each hold their own
			// curative items list but PotionEffects are created as needed;
			// they are not registered anywhere and there is no event for them
			// being created
			//
			// so.. just clear all potion effects instead of calling curePotionEffects
			player.clearActivePotions();
		}
		return super.onEaten(itemStack, world, player);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
		return super.onItemRightClick(itemStack, world, player);
	}
}
