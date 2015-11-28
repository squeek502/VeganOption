package squeek.veganoption.items;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.FluidHelper;

public class ItemRawSeitan extends Item
{
	public static IIcon partiallyWashedIcon;
	public static int NUM_WASHES_NEEDED = 4;
	public static int META_RAW = NUM_WASHES_NEEDED;

	public ItemRawSeitan()
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
		return getPercentWashed(itemStack) <= 0;
	}

	public static ItemStack wash(ItemStack itemStack, int amount)
	{
		int newMeta = Math.min(META_RAW, itemStack.getItemDamage() + amount);
		itemStack.setItemDamage(newMeta);
		return itemStack;
	}

	public static float getPercentWashed(ItemStack itemStack)
	{
		return (float) itemStack.getItemDamage() / NUM_WASHES_NEEDED;
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemStack)
	{
		return !isReadyToCook(itemStack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemStack)
	{
		return 1.0f - getPercentWashed(itemStack);
	}

	public static boolean tryWash(EntityItem entityItem)
	{
		if (entityItem == null || entityItem.worldObj.isRemote || entityItem.getEntityItem() == null)
			return false;

		if (!isReadyToCook(entityItem.getEntityItem()))
		{
			BlockHelper.BlockPos fluidBlockPos = BlockHelper.blockPos(entityItem.worldObj, MathHelper.floor_double(entityItem.posX), MathHelper.floor_double(entityItem.posY), MathHelper.floor_double(entityItem.posZ));
			FluidStack consumedFluid = FluidHelper.consumeExactFluid(fluidBlockPos, FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);

			if (consumedFluid != null)
			{
				EntityItem entityItemToWash = entityItem;
				ItemStack doughToWash = entityItemToWash.getEntityItem();

				if (entityItemToWash.getEntityItem().stackSize > 1)
				{
					doughToWash = entityItem.getEntityItem().splitStack(1);
					entityItemToWash = new EntityItem(entityItemToWash.worldObj, entityItemToWash.posX, entityItemToWash.posY, entityItemToWash.posZ, doughToWash);
					entityItemToWash.delayBeforeCanPickup = 10;
					entityItemToWash.worldObj.spawnEntityInWorld(entityItemToWash);
				}

				ItemStack washedItemStack = wash(doughToWash, 1);
				entityItemToWash.setEntityItemStack(washedItemStack);

				return true;
			}
		}
		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		return super.getUnlocalizedName(itemStack) + (!isReadyToCook(itemStack) ? "Unwashed" : "");
	}

	@Override
	public IIcon getIconFromDamage(int meta)
	{
		return meta != META_RAW ? partiallyWashedIcon : super.getIconFromDamage(meta);
	}

	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		partiallyWashedIcon = iconRegister.registerIcon(ModInfo.MODID_LOWER + ":seitan_raw_unwashed");
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subItems)
	{
		super.getSubItems(item, creativeTab, subItems);
		subItems.add(new ItemStack(item, 1, META_RAW));
	}
}
