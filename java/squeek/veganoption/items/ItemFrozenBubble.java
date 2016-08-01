package squeek.veganoption.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.FluidHelper;

public class ItemFrozenBubble extends Item
{
	public ItemFrozenBubble()
	{
		super();
		setHasSubtypes(true);
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
			return new ItemStack(Items.ENDER_PEARL);
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
			BlockPos fluidBlockPos = new BlockPos(MathHelper.floor_double(entityItem.posX), MathHelper.floor_double(entityItem.posY), MathHelper.floor_double(entityItem.posZ));
			FluidStack consumedFluid = FluidHelper.consumeExactFluid(entityItem.worldObj, fluidBlockPos, Ender.fluidRawEnder, FluidHelper.FINITE_FLUID_MB_PER_META);

			if (consumedFluid != null)
			{
				EntityItem entityItemToFill = entityItem;
				ItemStack bubbleToFill = entityItemToFill.getEntityItem();

				if (entityItemToFill.getEntityItem().stackSize > 1)
				{
					bubbleToFill = entityItem.getEntityItem().splitStack(1);
					entityItemToFill = new EntityItem(entityItemToFill.worldObj, entityItemToFill.posX, entityItemToFill.posY, entityItemToFill.posZ, bubbleToFill);
					entityItemToFill.setPickupDelay(10);
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
}
