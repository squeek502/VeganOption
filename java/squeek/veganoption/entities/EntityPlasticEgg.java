package squeek.veganoption.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import squeek.veganoption.network.MessageFX;
import squeek.veganoption.network.NetworkHandler;

import javax.annotation.Nonnull;

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
	protected void onImpact(@Nonnull RayTraceResult rayTraceResult)
	{
		if (rayTraceResult.entityHit != null)
		{
			rayTraceResult.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
		}

		if (!world.isRemote)
		{
			NetworkRegistry.TargetPoint target = new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 80);
			NetworkHandler.channel.sendToAllAround(new MessageFX(posX, posY, posZ, MessageFX.FX.PLASTIC_EGG_BREAK), target);
			setDead();
		}
	}
}
