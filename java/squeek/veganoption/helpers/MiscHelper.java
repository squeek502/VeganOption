package squeek.veganoption.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collection;

public class MiscHelper
{
	public static final int MAX_REDSTONE_SIGNAL_STRENGTH = 15;
	public static final int TICKS_PER_SEC = 20;
	public static final int TICKS_PER_DAY = 24000; // 20 minutes realtime

	public static boolean wildcardItemStacksMatch(ItemStack a, ItemStack b)
	{
		return OreDictionary.itemMatches(a, b, false) || OreDictionary.itemMatches(b, a, false);
	}

	public static boolean isItemStackInList(Collection<ItemStack> haystack, ItemStack needle)
	{
		return isItemStackInList(haystack, needle, false);
	}

	public static boolean isItemStackInList(Collection<ItemStack> haystack, ItemStack needle, boolean strict)
	{
		return getMatchingItemStackFromList(haystack, needle, strict) != null;
	}

	public static ItemStack getMatchingItemStackFromList(Collection<ItemStack> haystack, ItemStack needle)
	{
		return getMatchingItemStackFromList(haystack, needle, false);
	}

	public static ItemStack getMatchingItemStackFromList(Collection<ItemStack> haystack, ItemStack needle, boolean strict)
	{
		if (haystack != null)
		{
			for (ItemStack itemStack : haystack)
			{
				if (strict && OreDictionary.itemMatches(itemStack, needle, strict))
					return itemStack;
				else if (!strict && wildcardItemStacksMatch(itemStack, needle))
					return itemStack;
			}
		}
		return null;
	}

	public static Item getItemByName(String name)
	{
		return Item.REGISTRY.getObject(new ResourceLocation(name));
	}

	public static Block getBlockByName(String name)
	{
		return Block.REGISTRY.getObject(new ResourceLocation(name));
	}

	public static ItemStack getItemStackByObjectName(String name)
	{
		Item item = getItemByName(name);

		if (item != null)
			return new ItemStack(item);

		Block block = getBlockByName(name);

		if (block != null)
			return new ItemStack(block);

		return null;
	}
}
