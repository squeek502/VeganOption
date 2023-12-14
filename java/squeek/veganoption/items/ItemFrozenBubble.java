package squeek.veganoption.items;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.FluidHelper;

public class ItemFrozenBubble extends Item
{
	public ItemFrozenBubble()
	{
		super(new Item.Properties().durability(8));
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity)
	{
		tryFillWithRawEnderFromWorld(itemEntity);
		return super.onEntityItemUpdate(stack, itemEntity);
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
		itemStack.setDamageValue(itemStack.getDamageValue() + amount);
		if (isFull(itemStack))
		{
			return new ItemStack(Items.ENDER_PEARL);
		}
		return itemStack;
	}

	public static float getPercentFilled(ItemStack itemStack)
	{
		return itemStack.getDamageValue() / (float) itemStack.getMaxDamage();
	}

	public static int getMbPerFill(ItemStack itemStack)
	{
		return Ender.RAW_ENDER_PER_PEARL / itemStack.getMaxDamage();
	}

	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !isEmpty(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(getPercentFilled(stack) * 13f);
	}

	public static boolean tryFillWithRawEnderFromWorld(ItemEntity itemEntity)
	{
		if (itemEntity == null || itemEntity.level().isClientSide() || itemEntity.getItem().isEmpty())
			return false;

		if (!isFull(itemEntity.getItem()))
		{
			BlockPos fluidBlockPos = new BlockPos(Mth.floor(itemEntity.getX()), Mth.floor(itemEntity.getY()), Mth.floor(itemEntity.getZ()));
			FluidStack consumedFluid = FluidHelper.consumeExactFluid(itemEntity.level(), fluidBlockPos, Ender.rawEnderStill.get(), getMbPerFill(itemEntity.getItem()));

			if (consumedFluid != null)
			{
				ItemEntity itemEntityToFill = itemEntity;
				ItemStack bubbleToFill = itemEntityToFill.getItem();

				if (itemEntityToFill.getItem().getCount() > 1)
				{
					bubbleToFill = itemEntity.getItem().split(1);
					itemEntityToFill = new ItemEntity(itemEntityToFill.level(), itemEntityToFill.getX(), itemEntityToFill.getY(), itemEntityToFill.getZ(), bubbleToFill);
					itemEntityToFill.setPickUpDelay(10);
					itemEntityToFill.level().addFreshEntity(itemEntityToFill);
				}

				ItemStack filledItemStack = fill(bubbleToFill, 1);
				itemEntityToFill.setItem(filledItemStack);

				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescriptionId(ItemStack itemStack)
	{
		return super.getDescriptionId(itemStack) + (!isEmpty(itemStack) ? "_filled" : "");
	}
}
