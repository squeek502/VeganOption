package squeek.veganoption.items;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.helpers.RandomHelper;

import javax.annotation.Nonnull;

public class ItemSoapSolution extends Item
{
	public static final float BUBBLE_INITIAL_VELOCITY = 0.3f;

	public ItemSoapSolution()
	{
		super();
		setMaxStackSize(1);
		setMaxDamage(15); // 16 uses
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new ItemSoapSolution.DispenserBehavior());
		setNoRepair();
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack itemStack)
	{
		return EnumAction.DRINK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
	{
		return 16;
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack itemStack, World world, EntityLivingBase player)
	{
		if (!world.isRemote)
		{
			EntityBubble bubble = new EntityBubble(world, player);
			bubble.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, BUBBLE_INITIAL_VELOCITY, 1.0F);
			world.spawnEntity(bubble);
		}

		itemStack.damageItem(1, player);

		if (itemStack.getCount() == 0 && getContainerItem() != null)
		{
			return new ItemStack(getContainerItem());
		}

		return super.onItemUseFinish(itemStack, world, player);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		player.setActiveHand(hand);
		return super.onItemRightClick(world, player, hand);
	}

	public static class DispenserBehavior extends BehaviorProjectileDispense
	{
		@Nonnull
		@Override
		protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition iPosition, @Nonnull ItemStack stack)
		{
			return new EntityBubble(world, iPosition.getX(), iPosition.getY(), iPosition.getZ());
		}

		@Nonnull
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
		{
			// counteract the splitStack in super.dispenseStack
			itemStack.grow(1);
			super.dispenseStack(blockSource, itemStack);

			itemStack.attemptDamageItem(1, RandomHelper.random);
			if (itemStack.getItemDamage() >= itemStack.getMaxDamage())
			{
				itemStack.setCount(0);
			}
			if (itemStack.getCount() == 0 && itemStack.getItem().getContainerItem() != null)
			{
				return new ItemStack(itemStack.getItem().getContainerItem());
			}
			return itemStack;
		}

		// projectile velocity
		@Override
		protected float getProjectileVelocity()
		{
			return 0.5f;
		}
	}
}
