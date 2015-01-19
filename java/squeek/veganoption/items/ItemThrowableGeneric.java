package squeek.veganoption.items;

import java.lang.reflect.Constructor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import squeek.veganoption.helpers.RandomHelper;

public class ItemThrowableGeneric extends Item
{
	public Constructor<? extends EntityThrowable> thrownEntityThrowerConstructor = null;
	public Constructor<? extends EntityThrowable> thrownEntityCoordinatesConstructor = null;
	public Class<? extends EntityThrowable> thrownEntityClass;
	public String throwSound;

	public ItemThrowableGeneric(Class<? extends EntityThrowable> thrownEntityClass)
	{
		this(thrownEntityClass, "random.bow");
	}

	public ItemThrowableGeneric(Class<? extends EntityThrowable> thrownEntityClass, String throwSound)
	{
		super();
		this.thrownEntityClass = thrownEntityClass;
		this.throwSound = throwSound;

		try
		{
			thrownEntityThrowerConstructor = this.thrownEntityClass.getDeclaredConstructor(World.class, EntityLivingBase.class);
			thrownEntityCoordinatesConstructor = this.thrownEntityClass.getDeclaredConstructor(World.class, double.class, double.class, double.class);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!player.capabilities.isCreativeMode)
		{
			--itemStack.stackSize;
		}

		world.playSoundAtEntity(player, throwSound, 0.5F, 0.4F / (RandomHelper.random.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote)
		{
			world.spawnEntityInWorld(getNewThrownEntity(world, player));
		}

		return itemStack;
	}

	public EntityThrowable getNewThrownEntity(World world, EntityLivingBase thrower)
	{
		try
		{
			return thrownEntityThrowerConstructor.newInstance(world, thrower);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public EntityThrowable getNewThrownEntity(World world, double x, double y, double z)
	{
		try
		{
			return thrownEntityCoordinatesConstructor.newInstance(world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
