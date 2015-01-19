package squeek.veganoption.entities;

import squeek.veganoption.items.ItemThrowableGeneric;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class EntityThrowableGenericDispenserBehavior extends BehaviorProjectileDispense
{
	public ItemThrowableGeneric itemThrowableGeneric;

	public EntityThrowableGenericDispenserBehavior(ItemThrowableGeneric itemThrowableGeneric)
	{
		super();
		this.itemThrowableGeneric = itemThrowableGeneric;
	}

	@Override
	protected IProjectile getProjectileEntity(World world, IPosition position)
	{
		return itemThrowableGeneric.getNewThrownEntity(world, position.getX(), position.getY(), position.getZ());
	}
}
