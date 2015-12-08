package squeek.veganoption.items;

import java.util.Collection;
import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import squeek.veganoption.helpers.RandomHelper;

public class ItemSoap extends Item
{
	public static ItemStack milkBucket = new ItemStack(Items.milk_bucket);

	public ItemSoap()
	{
		super();
		setMaxStackSize(1);
		setMaxDamage(3); // 4 uses
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new ItemSoap.DispenserBehavior());
		setNoRepair();
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

	/**
	 * A way to cure only the potion effects that another item is a curative item of
	 */
	public static void curePotionEffectsAsItem(EntityLivingBase entity, ItemStack curativeItemToMimic)
	{
		entity.curePotionEffects(curativeItemToMimic);
	}

	/**
	 * A way to cure potion effects without clearing effects that are meant 
	 * to be uncurable (e.g. Thaumcraft warp)
	 */
	public static void cureAllCurablePotionEffects(EntityPlayer player)
	{
		@SuppressWarnings({"unchecked"})
		Collection<PotionEffect> activePotionEffects = player.getActivePotionEffects();
		for (PotionEffect potionEffect : activePotionEffects)
		{
			if (potionEffect.getCurativeItems().size() <= 0)
				continue;

			player.removePotionEffect(potionEffect.getPotionID());
		}
	}

	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			curePotionEffectsAsItem(player, milkBucket);
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
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);

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
				curePotionEffectsAsItem(mostDirtyEntity, milkBucket);

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
