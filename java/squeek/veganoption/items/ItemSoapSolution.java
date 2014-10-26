package squeek.veganoption.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import squeek.veganoption.entities.EntityBubble;

public class ItemSoapSolution extends Item
{
	public ItemSoapSolution()
	{
		super();
		setMaxStackSize(1);
		setMaxDamage(16);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack)
	{
		return EnumAction.drink;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
	{
		return 16;
	}

	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			world.spawnEntityInWorld(new EntityBubble(world, player));
		}

		itemStack.damageItem(1, player);

		if (itemStack.getItemDamage() == itemStack.getMaxDamage() && getContainerItem() != null)
		{
			return new ItemStack(getContainerItem());
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
