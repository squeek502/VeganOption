package squeek.veganoption.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFoodContainered extends ItemFood
{

	public ItemFoodContainered(int hunger, boolean isWolfsFavoriteFood)
	{
		super(hunger, isWolfsFavoriteFood);
	}

	public ItemFoodContainered(int hunger, float saturation, boolean isWolfsFavoriteFood)
	{
		super(hunger, saturation, isWolfsFavoriteFood);
	}

	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
	{
		ItemStack itemStackRemaining = super.onEaten(itemStack, world, player);

		if (getContainerItem() != null)
		{
			ItemStack container = getContainerItem(itemStack);
			if (itemStackRemaining == null || itemStackRemaining.stackSize <= 0)
				return container;
			else if (!player.inventory.addItemStackToInventory(container))
				player.dropPlayerItemWithRandomChoice(container, false);
		}

		return itemStackRemaining;
	}

}
