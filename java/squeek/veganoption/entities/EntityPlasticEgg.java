package squeek.veganoption.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.EggModifier;
import squeek.veganoption.network.MessageFX;
import squeek.veganoption.network.NetworkHandler;

public class EntityPlasticEgg extends EntityThrowable
{
	private ItemStack insideEgg;

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

	public EntityPlasticEgg(ItemStack insideEgg, World world, double x, double y, double z)
	{
		super(world, x, y, z);
		this.insideEgg = insideEgg;
	}

	public EntityPlasticEgg(ItemStack insideEgg, World world, EntityLivingBase thrower)
	{
		super(world, thrower);
		this.insideEgg = insideEgg;
	}

	@Override
	protected void onImpact(RayTraceResult rayTraceResult)
	{
		EggModifier eggModifier = Modifiers.eggs.findModifierForItemStack(insideEgg);

		if (rayTraceResult.entityHit != null)
		{
			eggModifier.onEntityCollision(rayTraceResult, this);
			rayTraceResult.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
		}

		if (!worldObj.isRemote)
		{
			eggModifier.onImpactGeneric(rayTraceResult, this);
			NetworkRegistry.TargetPoint target = new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 80);
			NetworkHandler.channel.sendToAllAround(new MessageFX(posX, posY, posZ, MessageFX.FX.PLASTIC_EGG_BREAK), target);
			setDead();
		}
	}
}
