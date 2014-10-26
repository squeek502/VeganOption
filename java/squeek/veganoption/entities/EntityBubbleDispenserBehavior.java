package squeek.veganoption.entities;

import squeek.veganoption.helpers.RandomHelper;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityBubbleDispenserBehavior extends BehaviorProjectileDispense
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
		return itemStack;
	}

	// projectile velocity
	@Override
	protected float func_82500_b()
	{
		return 0.5f;
	}
}
