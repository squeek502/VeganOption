package squeek.veganoption.items;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import squeek.veganoption.content.modules.Seitan;
import squeek.veganoption.helpers.FluidHelper;

public class ItemWashableWheat extends Item
{
	public ItemWashableWheat()
	{
		super(new Item.Properties());
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		if (tryWash(entity))
			return true;
		return super.onEntityItemUpdate(stack, entity);
	}

	public boolean isUnwashed(ItemStack itemStack)
	{
		return itemStack.getItem() == Seitan.seitanUnwashed.get();
	}

	public ItemStack wash(ItemStack itemStack)
	{
		if (itemStack.getItem() == Seitan.wheatFlour.get())
			return new ItemStack(Seitan.wheatDough.get());
		if (itemStack.getItem() == Seitan.wheatDough.get())
			return new ItemStack(Seitan.seitanUnwashed.get());
		if (itemStack.getItem() == Seitan.seitanUnwashed.get())
		{
			itemStack.setDamageValue(itemStack.getDamageValue() + 1);
			if (itemStack.getDamageValue() == 4)
				return new ItemStack(Seitan.seitanRaw.get());
			return itemStack;
		}
		return itemStack;
	}

	@Override
	public boolean isBarVisible(ItemStack itemStack)
	{
		return isUnwashed(itemStack);
	}

	@Override
	public int getBarWidth(ItemStack itemStack)
	{
		return super.getBarWidth(itemStack);
	}

	public boolean tryWash(ItemEntity entity)
	{
		if (entity == null || entity.level().isClientSide() || entity.getItem().isEmpty())
			return false;

		FluidStack consumedFluid = FluidHelper.consumeExactFluid(entity.level(), entity.blockPosition(), Fluids.WATER, FluidType.BUCKET_VOLUME);

		if (consumedFluid != null)
		{
			ItemEntity entityItemToWash = entity;
			ItemStack doughToWash = entityItemToWash.getItem();

			if (doughToWash.getCount() > 1)
			{
				doughToWash = doughToWash.split(1);
				entityItemToWash = new ItemEntity(entityItemToWash.level(), entityItemToWash.getBlockX(), entityItemToWash.getBlockY(), entityItemToWash.getBlockZ(), doughToWash);
				entityItemToWash.setPickUpDelay(10);
				entityItemToWash.level().addFreshEntity(entityItemToWash);
			}

			ItemStack washedItemStack = wash(doughToWash);
			entityItemToWash.setItem(washedItemStack);

			return true;
		}
		return false;
	}
}
