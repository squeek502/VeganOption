package squeek.veganoption.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.FluidHelper;

public class ItemFrozenBubble extends Item
{
	public static IIcon partiallyFilledIcon;

	public ItemFrozenBubble()
	{
		super();
		setHasSubtypes(true);
	}

	@Override
	public String getPotionEffect(ItemStack itemStack)
	{
		// water breathing
		return PotionHelper.field_151423_m;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem)
	{
		tryFillWithRawEnderFromWorld(entityItem);
		return super.onEntityItemUpdate(entityItem);
	}

	public static boolean isFull(ItemStack itemStack)
	{
		return getPercentFilled(itemStack) >= 1;
	}

	public static boolean isEmpty(ItemStack itemStack)
	{
		return getPercentFilled(itemStack) <= 0;
	}

	public static ItemStack fill(ItemStack itemStack, int amount)
	{
		itemStack.setItemDamage(itemStack.getItemDamage() + amount);
		if (isFull(itemStack))
		{
			return new ItemStack(Items.ender_pearl);
		}
		return itemStack;
	}

	public static float getPercentFilled(ItemStack itemStack)
	{
		return itemStack.getItemDamage() / 8f;
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemStack)
	{
		return !isEmpty(itemStack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemStack)
	{
		return 1.0f - getPercentFilled(itemStack);
	}

	public static boolean tryFillWithRawEnderFromWorld(EntityItem entityItem)
	{
		if (entityItem == null || entityItem.worldObj.isRemote || entityItem.getEntityItem() == null)
			return false;

		if (!isFull(entityItem.getEntityItem()))
		{
			BlockHelper.BlockPos fluidBlockPos = BlockHelper.blockPos(entityItem.worldObj, MathHelper.floor_double(entityItem.posX), MathHelper.floor_double(entityItem.posY), MathHelper.floor_double(entityItem.posZ));
			FluidStack consumedFluid = FluidHelper.consumeExactFluid(fluidBlockPos, Ender.fluidRawEnder, FluidHelper.FINITE_FLUID_MB_PER_META);

			if (consumedFluid != null)
			{
				EntityItem entityItemToFill = entityItem;
				ItemStack bubbleToFill = entityItemToFill.getEntityItem();

				if (entityItemToFill.getEntityItem().stackSize > 1)
				{
					bubbleToFill = entityItem.getEntityItem().splitStack(1);
					entityItemToFill = new EntityItem(entityItemToFill.worldObj, entityItemToFill.posX, entityItemToFill.posY, entityItemToFill.posZ, bubbleToFill);
					entityItemToFill.delayBeforeCanPickup = 10;
					entityItemToFill.worldObj.spawnEntityInWorld(entityItemToFill);
				}

				ItemStack filledItemStack = fill(bubbleToFill, 1);
				entityItemToFill.setEntityItemStack(filledItemStack);

				return true;
			}
		}
		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		return super.getUnlocalizedName(itemStack) + (!isEmpty(itemStack) ? "Filled" : "");
	}

	@Override
	public IIcon getIconFromDamage(int meta)
	{
		return meta > 0 ? partiallyFilledIcon : super.getIconFromDamage(meta);
	}

	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		partiallyFilledIcon = iconRegister.registerIcon(ModInfo.MODID_LOWER + ":frozen_bubble_filled");
	}
}
