package squeek.veganoption.items;

import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import squeek.veganoption.helpers.RandomHelper;

public class ItemSoap extends Item
{
	public ItemSoap()
	{
		super();
		setMaxStackSize(1);
		setMaxDamage(3); // 4 uses
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new ItemSoap.DispenserBehavior());
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack)
	{
		return EnumAction.eat;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
	{
		return 32;
	}

	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			// I can not figure out how the curative item system
			// is meant to be used at all. PotionEffects each hold their own
			// curative items list but PotionEffects are created as needed;
			// they are not registered anywhere and there is no event for them
			// being created
			//
			// so.. just clear all potion effects instead of calling curePotionEffects
			player.clearActivePotions();
		}

		itemStack.damageItem(1, player);

		return super.onEaten(itemStack, world, player);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
		return super.onItemRightClick(itemStack, world, player);
	}

	public static class DispenserBehavior extends BehaviorDefaultDispenseItem
	{
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
		{
			EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
			int x = blockSource.getXInt() + enumfacing.getFrontOffsetX();
			int y = blockSource.getYInt() + enumfacing.getFrontOffsetY();
			int z = blockSource.getZInt() + enumfacing.getFrontOffsetZ();
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double) x, (double) y, (double) z, (double) (x + 1), (double) (y + 1), (double) (z + 1));

			@SuppressWarnings("unchecked")
			List<EntityLivingBase> entitiesInFront = blockSource.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

			EntityLivingBase mostDirtyEntity = null;
			for (EntityLivingBase entityInFront : entitiesInFront)
			{
				if (mostDirtyEntity == null || entityInFront.getActivePotionEffects().size() > mostDirtyEntity.getActivePotionEffects().size())
					mostDirtyEntity = entityInFront;
			}
			if (mostDirtyEntity != null)
			{
				mostDirtyEntity.clearActivePotions();

				itemStack.attemptDamageItem(1, RandomHelper.random);
				if (itemStack.getItemDamage() >= itemStack.getMaxDamage())
				{
					itemStack.stackSize = 0;
				}
				return itemStack;
			}
			return super.dispenseStack(blockSource, itemStack);
		}
	}
}
