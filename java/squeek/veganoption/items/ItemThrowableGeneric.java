package squeek.veganoption.items;

import java.lang.reflect.Constructor;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import squeek.veganoption.helpers.RandomHelper;

public class ItemThrowableGeneric extends Item
{
	public static final float DEFAULT_THROWSPEED = 1.5F;

	public Constructor<? extends EntityThrowable> thrownEntityThrowerConstructor = null;
	public Constructor<? extends EntityThrowable> thrownEntityCoordinatesConstructor = null;
	public Class<? extends EntityThrowable> thrownEntityClass;
	public SoundEvent throwSound;
	public float throwSpeed;

	public ItemThrowableGeneric(Class<? extends EntityThrowable> thrownEntityClass)
	{
		this(thrownEntityClass, DEFAULT_THROWSPEED);
	}

	public ItemThrowableGeneric(Class<? extends EntityThrowable> thrownEntityClass, float throwSpeed)
	{
		this(thrownEntityClass, SoundEvents.ENTITY_ARROW_SHOOT, throwSpeed);
	}

	public ItemThrowableGeneric(Class<? extends EntityThrowable> thrownEntityClass, SoundEvent throwSound)
	{
		this(thrownEntityClass, throwSound, DEFAULT_THROWSPEED);
	}

	public ItemThrowableGeneric(Class<? extends EntityThrowable> thrownEntityClass, SoundEvent throwSound, float throwSpeed)
	{
		super();
		this.thrownEntityClass = thrownEntityClass;
		this.throwSound = throwSound;
		this.throwSpeed = throwSpeed;

		try
		{
			thrownEntityThrowerConstructor = this.thrownEntityClass.getDeclaredConstructor(World.class, EntityLivingBase.class);
			thrownEntityCoordinatesConstructor = this.thrownEntityClass.getDeclaredConstructor(World.class, double.class, double.class, double.class);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		catch (SecurityException e)
		{
			throw new RuntimeException(e);
		}

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new ItemThrowableGeneric.DispenserBehavior(this));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand)
	{
		if (!player.capabilities.isCreativeMode)
		{
			--itemStack.stackSize;
		}

		player.playSound(throwSound, 0.5F, 0.4F / (RandomHelper.random.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote)
		{
			EntityThrowable entity = getNewThrownEntity(world, player);
			entity.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, throwSpeed, 1.0F);
			world.spawnEntityInWorld(entity);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
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
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static class DispenserBehavior extends BehaviorProjectileDispense
	{
		public ItemThrowableGeneric itemThrowableGeneric;

		public DispenserBehavior(ItemThrowableGeneric itemThrowableGeneric)
		{
			super();
			this.itemThrowableGeneric = itemThrowableGeneric;
		}

		@Override
		protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack)
		{
			return itemThrowableGeneric.getNewThrownEntity(world, position.getX(), position.getY(), position.getZ());
		}
	}
}
