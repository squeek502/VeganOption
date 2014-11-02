package squeek.veganoption.helpers;

import java.util.Collection;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MiscHelper
{
	public static final int MAX_REDSTONE_SIGNAL_STRENGTH = 15;
	public static final int TICKS_PER_SEC = 20;
	public static final int TICKS_PER_DAY = 24000; // 20 minutes realtime

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
		for (ItemStack itemStack : haystack)
		{
			if (OreDictionary.itemMatches(itemStack, needle, strict))
				return itemStack;
		}
		return null;
	}
}
