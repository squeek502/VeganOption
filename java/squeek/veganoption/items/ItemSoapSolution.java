package squeek.veganoption.items;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.helpers.RandomHelper;

public class ItemSoapSolution extends Item
{
	public ItemSoapSolution()
	{
		super();
		setMaxStackSize(1);
		setMaxDamage(15); // 16 uses
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new ItemSoapSolution.DispenserBehavior());
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

		if (itemStack.stackSize == 0 && getContainerItem() != null)
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

	public static class DispenserBehavior extends BehaviorProjectileDispense
	{
		@Override
		protected IProjectile getProjectileEntity(World world, IPosition iPosition)
		{
			return new EntityBubble(world, iPosition.getX(), iPosition.getY(), iPosition.getZ());
		}

		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
		{
			// counteract the splitStack in super.dispenseStack
			itemStack.stackSize++;
			super.dispenseStack(blockSource, itemStack);

			itemStack.attemptDamageItem(1, RandomHelper.random);
			if (itemStack.getItemDamage() >= itemStack.getMaxDamage())
			{
				itemStack.stackSize = 0;
			}
			if (itemStack.stackSize == 0 && itemStack.getItem().getContainerItem() != null)
			{
				return new ItemStack(itemStack.getItem().getContainerItem());
			}
			return itemStack;
		}

		// projectile velocity
		@Override
		protected float func_82500_b()
		{
			return 0.5f;
		}
	}
}
