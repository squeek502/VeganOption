package squeek.veganoption.entities;

import squeek.veganoption.network.MessageFX;
import squeek.veganoption.network.NetworkHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPlasticEgg extends EntityThrowable
{
	public EntityPlasticEgg(World world)
	{
		super(world);
	}

	public EntityPlasticEgg(World world, EntityLivingBase thrower)
	{
		super(world, thrower);
	}

	public EntityPlasticEgg(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	@Override
	protected void onImpact(MovingObjectPosition movingObjectPosition)
	{
		if (movingObjectPosition.entityHit != null)
		{
			movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
		}

		if (!worldObj.isRemote)
		{
			TargetPoint target = new TargetPoint(dimension, posX, posY, posZ, 80);
			NetworkHandler.channel.sendToAllAround(new MessageFX(posX, posY, posZ, MessageFX.FX.PLASTIC_EGG_BREAK), target);
			setDead();
		}
	}
}
