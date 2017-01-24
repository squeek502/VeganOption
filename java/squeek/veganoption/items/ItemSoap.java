package squeek.veganoption.items;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import squeek.veganoption.helpers.RandomHelper;

import java.util.Collection;
import java.util.List;

public class ItemSoap extends Item
{
	public static ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET);

	public ItemSoap()
	{
		super();
		setMaxStackSize(1);
		setMaxDamage(3); // 4 uses
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new ItemSoap.DispenserBehavior());
		setNoRepair();
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack)
	{
		return EnumAction.EAT;
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

			player.removePotionEffect(potionEffect.getPotion());
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		if (!world.isRemote)
		{
			curePotionEffectsAsItem(entityLiving, milkBucket);
		}

		stack.damageItem(1, entityLiving);

		return super.onItemUseFinish(stack, world, entityLiving);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand)
	{
		player.setActiveHand(hand);
		return super.onItemRightClick(itemStack, world, player, hand);
	}

	public static class DispenserBehavior extends BehaviorDefaultDispenseItem
	{
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
		{
			EnumFacing enumfacing = blockSource.getWorld().getBlockState(blockSource.getBlockPos()).getValue(BlockDispenser.FACING);
			int x = (int) blockSource.getX() + enumfacing.getFrontOffsetX();
			int y = (int) blockSource.getY() + enumfacing.getFrontOffsetY();
			int z = (int) blockSource.getZ() + enumfacing.getFrontOffsetZ();
			AxisAlignedBB axisalignedbb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

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
