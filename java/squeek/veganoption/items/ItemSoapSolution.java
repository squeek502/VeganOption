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

	@Override
	public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase player)
	{
		if (!world.isRemote)
		{
			EntityBubble bubble = new EntityBubble(world, player);
			bubble.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, BUBBLE_INITIAL_VELOCITY, 1.0F);
			world.spawnEntityInWorld(bubble);
		}

		itemStack.damageItem(1, player);

		if (itemStack.stackSize == 0 && getContainerItem() != null)
		{
			return new ItemStack(getContainerItem());
		}

		return super.onItemUseFinish(itemStack, world, player);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand)
	{
		player.setActiveHand(hand);
		return super.onItemRightClick(itemStack, world, player, hand);
	}

	public static class DispenserBehavior extends BehaviorProjectileDispense
	{
		@Override
		protected IProjectile getProjectileEntity(World world, IPosition iPosition, ItemStack stack)
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
		protected float getProjectileVelocity()
		{
			return 0.5f;
		}
	}
}
