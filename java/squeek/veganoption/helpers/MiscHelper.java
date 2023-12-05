package squeek.veganoption.helpers;

import net.minecraft.world.item.Item;

import java.util.Collection;

public class MiscHelper
{
	public static final int MAX_REDSTONE_SIGNAL_STRENGTH = 15;
	public static final int TICKS_PER_SEC = 20;
	public static final int TICKS_PER_DAY = 24000; // 20 minutes realtime
	public static final int NINE_SLOT_WIDTH = 162;
	public static final int STANDARD_GUI_WIDTH = 176;
	public static final int STANDARD_SLOT_WIDTH = 18;

	public static Item getMatchingItemFromList(Collection<Item> haystack, Item needle)
	{
		if (haystack != null)
		{
			return haystack.contains(needle) ? needle : null;
		}
		return null;
	}
}
