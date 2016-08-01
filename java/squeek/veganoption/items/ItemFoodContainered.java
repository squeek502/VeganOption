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
	public void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player)
	{
		super.onFoodEaten(itemStack, world, player);

		if (getContainerItem() != null)
		{
			ItemStack container = getContainerItem(itemStack);
			if (itemStack == null || itemStack.stackSize <= 0)
				return;
			else if (!player.inventory.addItemStackToInventory(container))
				player.dropItem(container, false);
		}
	}

}
