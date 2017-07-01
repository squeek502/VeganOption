package squeek.veganoption.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.helpers.FluidHelper;

public class ItemWashableWheat extends Item
{
	public static final int META_FLOUR = 0;
	public static final int META_DOUGH = META_FLOUR + 1;
	public static final int META_UNWASHED_START = META_DOUGH + 1;
	public static final int NUM_WASHES_NEEDED = 4;
	public static final int META_UNWASHED_END = META_UNWASHED_START + NUM_WASHES_NEEDED;
	public static final int META_RAW = META_UNWASHED_END;

	public ItemWashableWheat()
	{
		super();
		setHasSubtypes(true);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem)
	{
		tryWash(entityItem);
		return super.onEntityItemUpdate(entityItem);
	}

	public static boolean isReadyToCook(ItemStack itemStack)
	{
		return getPercentWashed(itemStack) >= 1;
	}

	public static boolean isUnwashed(ItemStack itemStack)
	{
		int meta = itemStack.getItemDamage();
		return meta >= META_UNWASHED_START && meta < META_UNWASHED_END;
	}

	public static ItemStack wash(ItemStack itemStack, int amount)
	{
		int newMeta = Math.min(META_RAW, itemStack.getItemDamage() + amount);
		itemStack.setItemDamage(newMeta);
		return itemStack;
	}

	public static float getPercentWashed(ItemStack itemStack)
	{
		return (float) (itemStack.getItemDamage() - META_UNWASHED_START) / NUM_WASHES_NEEDED;
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemStack)
	{
		return isUnwashed(itemStack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemStack)
	{
		return 1.0f - getPercentWashed(itemStack);
	}

	public static boolean tryWash(EntityItem entityItem)
	{
		if (entityItem == null || entityItem.world.isRemote || entityItem.getItem().isEmpty())
			return false;

		if (!isReadyToCook(entityItem.getItem()))
		{
			BlockPos fluidBlockPos = new BlockPos(MathHelper.floor(entityItem.posX), MathHelper.floor(entityItem.posY), MathHelper.floor(entityItem.posZ));
			FluidStack consumedFluid = FluidHelper.consumeExactFluid(entityItem.world, fluidBlockPos, FluidRegistry.WATER, Fluid.BUCKET_VOLUME);

			if (consumedFluid != null)
			{
				EntityItem entityItemToWash = entityItem;
				ItemStack doughToWash = entityItemToWash.getItem();

				if (entityItemToWash.getItem().getCount() > 1)
				{
					doughToWash = entityItem.getItem().splitStack(1);
					entityItemToWash = new EntityItem(entityItemToWash.world, entityItemToWash.posX, entityItemToWash.posY, entityItemToWash.posZ, doughToWash);
					entityItemToWash.setPickupDelay(10);
					entityItemToWash.world.spawnEntity(entityItemToWash);
				}

				ItemStack washedItemStack = wash(doughToWash, 1);
				entityItemToWash.setItem(washedItemStack);

				return true;
			}
		}
		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		String baseName = super.getUnlocalizedName(itemStack);
		switch (itemStack.getItemDamage())
		{
			case META_FLOUR:
				return baseName + ".wheatFlour";
			case META_DOUGH:
				return baseName + ".wheatDough";
			case META_RAW:
				return baseName + ".seitanRaw";
			default:
				return baseName + ".seitanRawUnwashed";
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, NonNullList<ItemStack> subItems)
	{
		subItems.add(new ItemStack(item, 1, META_FLOUR));
		subItems.add(new ItemStack(item, 1, META_DOUGH));
		subItems.add(new ItemStack(item, 1, META_UNWASHED_START));
		subItems.add(new ItemStack(item, 1, META_RAW));
	}
}
